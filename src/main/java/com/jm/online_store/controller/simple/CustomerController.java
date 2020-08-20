package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@AllArgsConstructor
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    @GetMapping
    public String getCustomerPage() {
        return "customerPage";
    }

    @GetMapping("/profile")
    public String getPersonalInfo(Model model, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        User user = userService.findById(principal.getId()).get();
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateUserInfo(User user, Model model) {
        user.setRoles(Collections.singleton(roleService.findByName("ROLE_CUSTOMER").get()));
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
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("message", "Неверный старый пароль!");

            return "changePassword";
        }
        user.setPassword(newPassword);
        userService.updateUser(user);

        return "redirect:/customer/profile";
    }
}
