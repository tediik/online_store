package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainPageController {

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("userForm", new User());
        return "mainPage";
    }

}
