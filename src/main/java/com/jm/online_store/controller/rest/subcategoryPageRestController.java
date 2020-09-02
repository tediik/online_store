package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Рест контроллер страницы подкатегории.
 */
@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class subcategoryPageRestController {
    private final CategoriesService categoriesService;
    private final ProductService productService;

    @GetMapping("/category")
    public ResponseEntity<Long> getSubcategoryPage(@RequestParam("category") String category) {
        Long categoryId = categoriesService.getIdBySuperCategory(category);
        System.out.println(categoryId);
        List<Product> productsInCategory = new ArrayList<>();
        return ResponseEntity.ok().body(categoryId);
    }
}
