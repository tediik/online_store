package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.util.Transliteration;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoriesService categoriesService;

    /**
     * Создаёт мапу - ключ - название категории, значение - мапа с названиями подкатегории.
     * Во внутренних мапах - ключ - подкатегория кириллицей и значение - латиницей.
     *
     * @return Пример: {"Компьютеры":{"Комплектующие":"Komplektuyushchiye",
     * "Компьютеры":"Kompʹyutery",
     * "Ноутбуки":"Noutbuki"},
     * "Смартфоны и гаджеты":{"Планшеты":"Planshety",
     * "Смартфоны":"Smartfony"}}
     */
    @GetMapping("/categories")
    @ApiOperation(value = "Creates hashmap: key - category name, value - map with subcategories name. In the inner map key is subcategory in kirillic, the value - in latin. example: " +
            "{\"Компьютеры\":{\"Комплектующие\":\"Komplektuyushchiye\",\n" +
            "     * \"Компьютеры\":\"Kompʹyutery\",\n" +
            "     * \"Ноутбуки\":\"Noutbuki\"},\n" +
            "     * \"Смартфоны и гаджеты\":{\"Планшеты\":\"Planshety\",\n" +
            "     * \"Смартфоны\":\"Smartfony\"}}")

    public ResponseEntity<Map<String, Map<String, String>>> getCategories() {
        List<Categories> categoriesFromDB = categoriesService.findAll();
        Map<String, Map<String, String>> categoriesBySuperCategories = new HashMap<>();

        for (Categories category : categoriesFromDB) {
            Map<String, String> innerMap = new HashMap<>();
            innerMap.put(category.getCategory(), Transliteration.сyrillicToLatin(category.getCategory()));
            categoriesBySuperCategories.merge(categoriesService.getCategoryById(category.getParentCategoryId()).orElse(category).getCategory(), innerMap,
                    (oldV, newV) -> Stream.concat(oldV.entrySet().stream(), newV.entrySet().stream())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }
        return ResponseEntity.ok(categoriesBySuperCategories);
    }

    /**
     * Метод поиска всех категорий в БД
     *
     * @return список Categories
     */

    @GetMapping("/categories/categories")
    @ApiOperation(value = "Get all subcategories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        return ResponseEntity.ok(categoriesService.findAll());
    }

}
