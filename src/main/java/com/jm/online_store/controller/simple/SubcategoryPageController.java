package com.jm.online_store.controller.simple;

import com.jm.online_store.controller.rest.SubcategoryPageRestController;
import com.jm.online_store.service.interf.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/categories")
public class SubcategoryPageController {

    private final CategoriesService categoriesService;

    @GetMapping("/{categoryName}")
    public String getSubcategoryPage(@PathVariable(value = "categoryName") String categoryName) {
        if (!categoriesService.getCategoryByCategoryName(categoryName).isPresent()) {
            return "/error/404";
        }
        SubcategoryPageRestController.setCategoryName(categoryName);
        return "subcategoryPage";
    }
}
