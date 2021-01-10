package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityController {
    /**
     * Контролллер для ролей ADMIN MANAGER
     */
    private final UserService userService;

    @GetMapping
    public String homePage() {
        return "admin-page";
    }

    @GetMapping("/profile")
    public String getPersonalInfo(Model model) {
        User user = userService.getCurrentLoggedInUser();
        model.addAttribute("user", user);
        return "profile-authority";
    }

    @PostMapping("/profile")
    public String updateUserProfile(User user, Model model) {
        User updateUser = userService.updateUserProfile(user);
        model.addAttribute("user", updateUser);
        return "profile-authority";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Model model,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        User user = userService.getCurrentLoggedInUser();
        if (!userService.changePassword(user.getId(), oldPassword, newPassword)) {
            model.addAttribute("message", "Pls, check your old password!");
        }
        return "redirect:/authority/profile";
    }

    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateNewUsersMail(token, request);
        model.addAttribute("message", "Email address changes successfully");
        return "redirect:/authority/profile";
    }
}
