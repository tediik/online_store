package com.jm.online_store.controller.rest.manager;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления категориями товаров со страницы менеджера
 */
@RestController
@RequestMapping("api/categories")
@AllArgsConstructor
@Slf4j
public class ProductCategoriesRestController {
    private final CategoriesService categoriesService;

    /**
     * Возвращает список всех категорий
     */
    @GetMapping("/all")
    public ResponseEntity<ArrayNode> getAllCategories() {
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }

    /**
     * Возвращает категорию по id
     * @param id
     */
    @GetMapping("/getOne/{id}")
    public ResponseEntity<String> getCategoryNameByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.getCategoryNameByProductId(id));
    }

    /**
     * Возвращает список подкатегорий для корневой категории
     */
    @GetMapping("/sub/{id}")
    public ResponseEntity<List<Categories>> getSubCategoriesById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.getCategoriesByParentCategoryId(id));
    }

    /**
     * Сохраняет категорию
     */
    @PostMapping
    public ResponseEntity<Categories> newCategory(@RequestBody Categories categories) {
        categoriesService.saveCategory(categories);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Обновление категории
     */
    @PutMapping
    public ResponseEntity<Categories> updateCategory(@RequestBody Categories categories) {
        categoriesService.saveCategory(categories);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Удаление категории
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Categories> deleteCategory(@PathVariable Long id) {
        categoriesService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
