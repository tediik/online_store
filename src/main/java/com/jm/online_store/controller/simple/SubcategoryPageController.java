package com.jm.online_store.controller.simple;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер подкатегорий продуктов
 */
@Controller
@AllArgsConstructor
@RequestMapping("/categories")
public class SubcategoryPageController {

    @GetMapping("/{categoryName}")
    public String getSubcategoryPage() {
        return "subcategoryPage";
    }
}
