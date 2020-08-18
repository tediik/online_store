package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestRegController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;


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
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordConfirmError", "Пароли не совпадают");
            return "test-reg";
        }
        if (userService.isExist(userForm.getEmail())){
            model.addAttribute("emailError", "Пользователь с таким именем уже существует");
            return "test-reg";
        }

        userService.addUser(userForm);
        model.addAttribute("message", "User added successfully!");
        return "successfulRegister";
    }
}
