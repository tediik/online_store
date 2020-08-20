package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TestRegController {

    private final UserService userService;

    @GetMapping("/test-reg")
    public String testReg(Model model) {
        model.addAttribute("userForm", new User());
        return "test-reg";
    }

    @PostMapping("/test-reg")
    public String registerUserAccount(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "test-reg";
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            model.addAttribute("passwordConfirmError", "Пароли не совпадают");
            return "test-reg";
        }
        if (userService.isExist(userForm.getEmail())) {
            model.addAttribute("emailError", "Пользователь с таким именем уже существует");
            return "test-reg";
        }

        userService.addUser(userForm);
        model.addAttribute("message", "User added successfully!");
        return "successfulRegister";
    }
}
