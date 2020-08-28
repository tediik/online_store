package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Рест контроллер главной страницы.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class MainPageRestController {

    private final CategoriesService categoriesService;

    @GetMapping("/categories")
    public ResponseEntity<List<Categories>> getCategories() {
        List<Categories> categoriesFromDB = categoriesService.getAllCategories();
        Map<String, List<String>> categoriesBySuperCategories = new HashMap<>();
        for (Categories category : categoriesFromDB) {
            categoriesBySuperCategories.merge(category.getSuperCategory(), Arrays.asList(category.getCategory()),
                    (oldV, newV) -> Stream.concat(oldV.stream(), newV.stream()).collect(Collectors.toList()));
        }
        return new ResponseEntity<>(categoriesFromDB, HttpStatus.OK);
    }
}
