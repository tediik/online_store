package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.util.Transliteration;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private static String categoryName;

    public static void setCategoryName(String categoryName) {
        subcategoryPageRestController.categoryName = categoryName;
    }

    @GetMapping("/category")
    public ResponseEntity<Categories> getCategory() {
        Categories category = categoriesService.getCategoryByCategoryName(Transliteration.latinToCyrillic(categoryName).replaceAll("_"," ")).get();
        return ResponseEntity.ok().body(category);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProductsInCategory() {
        Long categoryId = categoriesService.getCategoryByCategoryName(categoryName).get().getId();
//        List<Product> products = productService.findAllByCategory(categoryId);
        List<Product> products = new ArrayList<>();
        return ResponseEntity.ok(products);
    }
}
