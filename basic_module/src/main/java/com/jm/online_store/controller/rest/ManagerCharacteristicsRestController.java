package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.ProductCharacteristicDto;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
