package com.jm.online_store.controller.simple;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для отображения страницы товаров в кабинете менеджера
 */
@Controller
@RequestMapping("/manager")
@AllArgsConstructor
public class ManagerProductsController {

    /**
     * Метод выводит страницу товаров
     */
    @GetMapping("/products")
    public String getProducts() {
        return "products";
    }
}

