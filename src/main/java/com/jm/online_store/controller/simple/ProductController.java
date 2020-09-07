package com.jm.online_store.controller.simple;

import com.jm.online_store.controller.rest.ProductRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public String getSubcategoryPage(@PathVariable(value = "id") Long productId) {
        ProductRestController.setProductId(productId);
        return "productPage";
    }
}
