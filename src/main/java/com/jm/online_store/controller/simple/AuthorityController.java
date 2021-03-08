package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityController {
    /**
     * Контроллер для ролей ADMIN MANAGER
     */
    private final UserService userService;

    @GetMapping
    public String homePage() {
        return "admin-page";
    }

    @GetMapping("/profile")
    public String getPersonalInfo() {
        return "profile-authority";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "change-password";
    }

    @GetMapping("/activatenewmail/{token}")
    public String changeMail(@PathVariable String token, HttpServletRequest request) {
        userService.activateNewUsersMail(token, request);
        return "redirect:/authority/profile";
    }
}
