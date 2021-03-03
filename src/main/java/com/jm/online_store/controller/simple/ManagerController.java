package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {
    private final UserService userService;
    private final CategoriesService categoriesService;

    @GetMapping
    public String getManagerPage() {
        return "manager-page";
    }

    @GetMapping("/news")
    public String getNewsManagementPage() {
        return "news-management";
    }

    @GetMapping("/reports")
    public String getReportsPage() {
        return "reports";
    }

    @GetMapping("/settings")
    public String getSettingsPage (){
        return "manager-settings";
    }

    @GetMapping("/stocks")
    public String getStocks() {
        return "stocks-manager-page";
    }

    @GetMapping("/mailing")
    public String getMailing() {
        return "manager-mailing";
    }

    @GetMapping("/feedback")
    public String getFeedbacks() {
        return "manager-feedback";
    }

    @GetMapping("/shops")
    public String getShops() {
        return "manager-shops";
    }

    @GetMapping("/rating")
    public String getRating() {
        return "products-rating";
    }

    @GetMapping("/characteristics")
    public String getCharacteristics() {
        return "characteristics";
    }

    /*
     добавил маппинг для отображения профиля на странице менеджера
     */
    @GetMapping("/profile")
    public String getPersonalInfo() {
        return "manager-profile";
    }
}
