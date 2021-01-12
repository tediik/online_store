package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.dto.ProductCharacteristicDto;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для работы с характеристиками товаров
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ManagerCharacteristicsRestController {

    private final ProductService productService;
    private final CharacteristicService characteristicService;
    private final ProductCharacteristicService productCharacteristicService;
    private final CategoriesService categoriesService;

    /**
     * Метод выводит список всех характеристик
     *
     * @return List<Characteristic>> возвращает список характеристик
     */
    @GetMapping(value = "/manager/characteristics/allCharacteristics")
    public List<Characteristic> findAll() {

        return characteristicService.findAll();
    }

    /**
     * Метод добавляет характеристику
     *
     * @param characteristic характеристика для добавления
     * @return ResponseEntity<Characteristic> Возвращает добавленную харакетристику с кодом ответа
     */
    @PostMapping(value = "/rest/characteristics/addCharacteristic")
    public ResponseEntity<Characteristic> addCharacteristic(@RequestBody Characteristic characteristic) {

        if (characteristic.getCharacteristicName().equals("")) {
            log.debug("EmptyCharacteristicName");
            return new ResponseEntity("EmptyCharacteristicName", HttpStatus.BAD_REQUEST);
        }

        characteristicService.saveCharacteristic(characteristic);
        return ResponseEntity.ok(characteristic);
    }

    /**
     * Метод возвращает характеристику по id
     *
     * @param id - characteristic id (Long)
     * @return ResponseEntity(characteristic, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/manager/characteristic/{id}")
    public ResponseEntity<Characteristic> getCharacteristic(@PathVariable Long id) {
        if (characteristicService.findCharacteristicById(id).isEmpty()) {
            log.debug("Characteristic with id: {} not found", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Characteristic characteristic = characteristicService.findCharacteristicById(id).orElseThrow(CharacteristicNotFoundException::new);
        return new ResponseEntity<>(characteristic, HttpStatus.OK);
    }

    /**
     * Метод для Изменения характеристики
     *
     * @param characteristic
     * @return new ResponseEntity<>(HttpStatus)
     */
    @PutMapping(value = "/manager/characteristics")
    public ResponseEntity<Characteristic> editCharacteristic(@RequestBody Characteristic characteristic) {
        if (characteristicService.findCharacteristicById(characteristic.getId()).isEmpty()) {
            log.debug("There are no characteristic with id: {}", characteristic.getId());
            return ResponseEntity.noContent().build();
        }

        characteristicService.updateCharacteristic(characteristic);
        log.debug("Changes to characteristic with id: {} was successfully added", characteristic.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Метод для удаления характеристики
     *
     * @param id       - id харакетристики для удаления
     * @param category - имя категории
     * @return ResponseEntity<>(HttpStatus)
     */
    @DeleteMapping(value = "/manager/characteristics/{id}/{category}")
    public ResponseEntity<Characteristic> deleteCharacteristic(@PathVariable Long id,
                                                               @PathVariable String category) {
        if (category.equals("default")) {
            characteristicService.deleteByID(id);
            log.debug("Characteristic with id: {}, was deleted successfully", id);

        } else {
            characteristicService.deleteByIDInSelectedCategory(id, category);

        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Метод, который возвращает характеристи для выбранной категории
     *
     * @param categoryId id нужной категории
     * @return List<Characteristic> лист харктеристик
     */
    @GetMapping("manager/characteristics/{categoryId}")
    public List<Characteristic> getCharacteristicsByCategory(@PathVariable Long categoryId) {
        return characteristicService.findByCategoryId(categoryId);
    }

    /**
     * Метод, который возвращает характеристи для выбранной категории
     *
     * @param category имя нужной категории
     * @return List<Characteristic> лист харктеристик
     */
    @GetMapping("manager/characteristicsByCategoryName/{category}")
    public List<Characteristic> getCharacteristicsByCategoryName(@PathVariable String category) {
        if (category.equals("default")) {
            return characteristicService.findAll();
        }
        return characteristicService.findByCategoryName(category);
    }

    /**
     * Метод добавляет характеристики, только что добавленному, товару
     *
     * @param addedProductName название добавленного товара
     * @return ResponseEntity<ProductCharacteristic> Возвращает добавленный товар с кодом ответа
     */
    @PostMapping(value = "/manager/characteristics/addCharacteristics/{addedProductName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Long>> addCharacteristics(@RequestBody ProductCharacteristicDto[] productCharacteristicsDto,
                                                         @PathVariable String addedProductName) {
        List<Long> productCharacteristicIds = new ArrayList<>();

        for (ProductCharacteristicDto productCharacteristicDto : productCharacteristicsDto) {
            if (!productCharacteristicDto.getValue().equals("")) {
                productCharacteristicIds.add(productCharacteristicService.addProductCharacteristic(
                        productService.findProductByName(addedProductName).orElseThrow(ProductNotFoundException::new).getId(),
                        productCharacteristicDto.getCharacteristicId(), productCharacteristicDto.getValue()));
            }
        }

        return ResponseEntity.ok(productCharacteristicIds);
    }

    /**
     * Метод возвращает список всех характеристик, кроме характеристик выбранной категории
     *
     * @param categoryName наименование хаарктеристики
     * @return List<Characteristic>> возвращает список характеристик
     */
    @GetMapping(value = "/manager/characteristics/otherThenSelected/{categoryName}")
    public List<Characteristic> findAllOtherThenSelected(@PathVariable String categoryName) {
        List<Characteristic> characteristicsSelectedCategoty = characteristicService.findByCategoryName(categoryName);
        List<Characteristic> allCharacteristics = characteristicService.findAll();
        allCharacteristics.removeAll(characteristicsSelectedCategoty);
        return allCharacteristics;
    }

    /**
     * Метод добавляет характеристики выбранной категории
     *
     * @param selectedCategory название выбранной категории
     * @return ResponseEntity<List < Characteristic>> Возвращает добавленные харакетристики с кодом ответа
     */
    @PostMapping(value = "/manager/characteristics/addCharacteristicsToCategory/{selectedCategory}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Characteristic>> addCharacteristicsToCategory(@RequestBody Characteristic[] characteristics,
                                                                             @PathVariable String selectedCategory) {
        Categories category = categoriesService.getCategoryByCategoryName(selectedCategory).get();
        List<Characteristic> characteristicsSelectedCategory = category.getCharacteristics();
        for (Characteristic characteristic : characteristics) {
            characteristicsSelectedCategory.add(characteristicService.findByCharacteristicName(characteristic.getCharacteristicName())
                    .orElseThrow(CharacteristicNotFoundException::new));
        }
        category.setCharacteristics(characteristicsSelectedCategory);
        categoriesService.saveCategory(category);

        return ResponseEntity.ok(characteristicsSelectedCategory);
    }
}
