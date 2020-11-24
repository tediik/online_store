package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final ProductService productService;
    private final CategoriesService categoriesService;

    @Autowired
    public ManagerController(ProductService productService, CategoriesService categoriesService) {
        this.productService = productService;
        this.categoriesService = categoriesService;
    }

    @GetMapping
    public String getManagerPage() {
        return "managerPage";
    }

    @GetMapping("/news")
    public String getNewsManagementPage() {
        return "newsManagement";
    }

    @GetMapping("/reports")
    public String getReportsPage() {
        return "reports";
    }

    @GetMapping("/settings")
    public String getSettingsPage (){
        return "manager_settings";
    }

    @GetMapping("/stocks")
    public String getStocks() {
        return "stocksManagerPage";
    }

    @GetMapping("/feedback")
    public String getFeedbacks() {
        return "managerFeedback";
    }

    @GetMapping("/rating")
    public String getRating(Model model) {
        model.addAttribute("listCategories", categoriesService.findAll().stream().map(Categories::getCategory).collect(Collectors.toList()));

        return "productsRating";
    }
}
