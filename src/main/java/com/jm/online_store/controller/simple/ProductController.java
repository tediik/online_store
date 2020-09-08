package com.jm.online_store.controller.simple;

import com.jm.online_store.controller.rest.ProductRestController;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public String getProductPage(@PathVariable(value = "id") Long productId) {
        if (!productService.findProductById(productId).isPresent()) {
            return "/error/404";
        }
        ProductRestController.setProductId(productId);
        return "productPage";
    }
}
