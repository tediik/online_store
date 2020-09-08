package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.util.Transliteration;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер страницы подкатегории.
 */
@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
public class SubcategoryPageRestController {
    private final CategoriesService categoriesService;

    private static String categoryName;

    public static void setCategoryName(String categoryName) {
        SubcategoryPageRestController.categoryName = categoryName;
    }

    @GetMapping("/category")
    public ResponseEntity<Categories> getCategory() {
        Categories category = categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(categoryName).replaceAll("_"," ")).get();
        return ResponseEntity.ok().body(category);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Categories>> getAllCategories() {
        List<Categories> categories = categoriesService.getAllCategories();
        return ResponseEntity.ok().body(categories);
    }
}
