package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainPageController {

    private final ProductService productService;

    public MainPageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/")
    public String mainPage(Model model) {
        model.addAttribute("products", productService.findAll());
        return "mainPage";
    }
}
