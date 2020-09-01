package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class subcategoryPageController {

    @GetMapping("/{categoryName}")
    public String getSubcategoryPage(@PathVariable(value = "categoryName") String categoryName) {

        return "subcategoryPage";
    }
}
