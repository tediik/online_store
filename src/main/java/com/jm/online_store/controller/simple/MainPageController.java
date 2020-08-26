package com.jm.online_store.controller.simple;

import com.jm.online_store.model.GenericResponce;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
@Slf4j
public class MainPageController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    private ValidationUtils validationUtils;

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("userForm", new User());
        return "mainPage";
    }

    @PostMapping("/registration")
    @ResponseBody
    public GenericResponce registerUserAccount(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.debug("BindingResult in registerUserAccount hasErrors: {}", bindingResult);
            return new GenericResponce("");
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            log.debug("Passwords do not match : passwordConfirmError");
            return new GenericResponce("passwordError");
        }
        if (userService.isExist(userForm.getEmail())){
            log.debug("User with same email already exists");
            return new GenericResponce("duplicatedEmailError");
        }
        if (validationUtils.isNotValidEmail(userForm.getEmail())){
            log.debug("Wrong email! Не правильно введен email");
            return new GenericResponce("notValidEmailError");
        }
        userService.regNewAccount(userForm);
        return new GenericResponce("success");
    }

    @GetMapping("/activate/{token}")
    public String activate(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateUser(token, request);
        return "redirect:/customer";
    }
}
