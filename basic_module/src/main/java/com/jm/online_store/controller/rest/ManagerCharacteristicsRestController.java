package com.jm.online_store.controller.rest;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для работы с характеристиками товаров
 */
@RestController
@AllArgsConstructor
@Slf4j
public class ManagerCharacteristicsRestController {

    private final ProductService productService;
    private final CategoriesService categoriesService;
    private final CharacteristicService characteristicService;
    private final ProductCharacteristicService productCharacteristicService;

    /**
     * Метод выводит список всех характеристик
     *
     * @return List<Characteristic>> возвращает список товаров
     */
    @GetMapping(value = "/manager/characteristics/allCharacteristics")
    public List<Characteristic> findAll() {
        List<Characteristic> list = characteristicService.findAll();
        return characteristicService.findAll();
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
        Characteristic characteristic = characteristicService.findCharacteristicById(id).get();
        return new ResponseEntity<>(characteristic, HttpStatus.OK);
    }

    /**
     * Метод для Изменения характеристики
     *
     * @param characteristic
     * @return new ResponseEntity<>(HttpStatus)
     */
    @PutMapping(value = "/manager/characteristics")
    public ResponseEntity editCharacteristic(@RequestBody Characteristic characteristic) {
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
     * @param id - id харакетристики для удаления
     * @return ResponseEntity<>(HttpStatus)
     */
    @DeleteMapping(value = "/manager/characteristics/{id}")
    public ResponseEntity<Characteristic> deleteCharacteristic(@PathVariable Long id) {
        try {
            characteristicService.deleteByID(id);
        } catch (IllegalArgumentException e) {
            log.debug("There is no characteristic with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.debug("Characteristic with id: {}, was deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Метод, который возвращает характеристи для выбранной категории
     *
     * @param categoryId id нужной категории товаров
     * @return List<Characteristic> лист харктеристик
     */
    @GetMapping("manager/characteristics/{categoryId}")
    public List<Characteristic> getCharacteristicsByCategory(@PathVariable Long categoryId) {
        return characteristicService.findByCategoryId(categoryId);
    }

    /**
     * Метод добавляет характеристики, только что добавденному, товару
     *
     * @param addedProductName название добавденного товара
     * @return ResponseEntity<ProductCharacteristic> Возвращает добавленный товар с кодом ответа
     */
    @PostMapping(value = "/manager/characteristics/addCharacteristics/{addedProductName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Long>> addCharacteristics(@RequestBody ProductCharacteristicDto[] productCharacteristicsDto,
                                                         @PathVariable String addedProductName) {
        List<Long> productCharacteristicIds = new ArrayList<>();

        for (ProductCharacteristicDto productCharacteristicDto : productCharacteristicsDto) {
            if (!productCharacteristicDto.getValue().equals("")) {
                productCharacteristicIds.add(productCharacteristicService.addProductCharacteristic(
                        productService.findProductByName(addedProductName).get().getId(),
                        productCharacteristicDto.getCharacteristicId(), productCharacteristicDto.getValue()));
            }
        }

        return ResponseEntity.ok(productCharacteristicIds);
    }
}
