package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    private static Long productId;

    public static void setProductId(Long productId) {
        ProductRestController.productId = productId;
    }

    @GetMapping("/product")
    public ResponseEntity<Product> getProduct() {
        Product product = productService.findProductById(productId).get();
        return ResponseEntity.ok().body(product);
    }
}
