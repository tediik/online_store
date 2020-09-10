package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Рест контроллер страницы продукта.
 */
@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductRestController {

    private ProductService productService;

    /**
     * Ищет продукт в БД по id из пути
     *
     * @param id продукта
     * @return сущность Product, если продукт с таким id существует
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        if (!productService.findProductById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Product product = productService.findProductById(id).get();
        return ResponseEntity.ok(product);
    }
}
