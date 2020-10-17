package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер страницы продукта.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("manager/api/categories")
public class CategoryRestController {

    private final CategoriesService categoriesService;
    private final ProductService productService;

    /**
     * Ищет категории в БД
     *
     * @return список сущностей Categories
     */
    @GetMapping("/all")
    public ResponseEntity<List<Categories>> getAllCategories() {
        return new ResponseEntity<>(categoriesService.getAllCategories(), HttpStatus.OK);
    }

    /**
     * Ищет категорию продукта по id
     *
     * @param prodId id продукта
     * @return Categories продукта
     */
    @GetMapping("/{prodId}")
    public ResponseEntity<Categories> getCategoryByProduct(@PathVariable Long prodId){
        return new ResponseEntity<>(productService.findProductCategory(prodId),HttpStatus.OK);
    }

}