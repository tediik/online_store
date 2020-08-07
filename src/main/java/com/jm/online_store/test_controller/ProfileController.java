package com.jm.online_store.test_controller;

import com.jm.online_store.DTO.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class ProfileController {
    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registration";
    }

    @GetMapping("/user/profile")
    public String showProfile(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "profile";
    }
}
