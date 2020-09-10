package com.jm.online_store.controller.simple;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер продукта
 */
@Controller
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public String getProductPage(@PathVariable(value = "id") Long productId) {
        return "productPage";
    }
}
