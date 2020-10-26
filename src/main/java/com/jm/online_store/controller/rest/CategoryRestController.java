package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер категорий.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("manager/api/categories")
public class CategoryRestController {

    private final CategoriesService categoriesService;

    /**
     * Метод, который ищет категории в БД
     *
     * @return список сущностей Categories
     */
    @GetMapping("/all")
    public ResponseEntity<List<Categories>> getAllCategories() {
        return new ResponseEntity<>(categoriesService.getAllCategories(), HttpStatus.OK);
    }
}
