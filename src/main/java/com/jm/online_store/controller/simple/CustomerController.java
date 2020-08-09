package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Gender;
import com.jm.online_store.model.User;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getCustomerPage() {
        return "customerPage";
    }

    @GetMapping("/profile")
    public String getPersonalInfo(Model model, Authentication auth) {
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateUserInfo(User user, Model model) {
        userService.updateUser(user);
        model.addAttribute("user", user);

        return "/profile";
    }

    @GetMapping("/change-password")
    public String changePassword() {

        return "changePassword";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication auth, Model model,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        User user = (User) auth.getPrincipal();
        if (!user.getPassword().equals(oldPassword)) {
            model.addAttribute("message", "Неверный старый пароль!");

            return "changePassword";
        }
        user.setPassword(newPassword);
        userService.updateUser(user);

        return "redirect:/customer/profile";
    }
}
