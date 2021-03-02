package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {
    private final UserService userService;
    private final CategoriesService categoriesService;

    @GetMapping
    public String getManagerPage(Model model) {
        User user = userService.getCurrentLoggedInUser();
        model.addAttribute("user", user);
        model.addAttribute("listCategories", categoriesService.getCategoriesWithoutParentCategory().stream().map(Categories::getCategory).collect(Collectors.toList()));
        return "manager-page";
    }
    @GetMapping("/shops")
    public String getShops() {
        return "manager-shops";
    }
}
