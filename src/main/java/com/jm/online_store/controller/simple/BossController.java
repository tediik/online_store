package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

@Controller
@RequestMapping("/boss")
@RequiredArgsConstructor
public class BossController {

    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String homePage() {
        return "adminPage";
    }

    @GetMapping("/profile")
    public String getPersonalInfo(Model model, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        User user = userService.findById(principal.getId()).get();
        model.addAttribute("user", user);
        return "profileBoss";
    }

    @PostMapping("/profile")
    public String updateUserProfile(User user, Model model) {
        User updateUser = userService.updateUserProfile(user);
        model.addAttribute("user", updateUser);
        return "profileBoss";
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
        if (!userService.changePassword(user.getId(), oldPassword, newPassword)) {
            model.addAttribute("message", "Pls, double check previous password!");
        }
        return "redirect:/boss/profile";
    }

    @PostMapping("/uploadImage")
    @ResponseBody
    public String handleImagePost(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateUserImage(userDetails.getId(), imageFile);
    }

    @DeleteMapping("/deleteImage")
    @ResponseBody
    public String deleteImage() throws IOException {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.deleteUserImage(userDetails.getId());
    }

    @PostMapping("/changemail")
    public String changeMailReq(Authentication auth, Model model,
                                @RequestParam String newMail) {
        User user = (User) auth.getPrincipal();
        userService.changeUsersMail(user, newMail);
        model.addAttribute("message", "Please check your email!");
        return "redirect:/boss/profile";
    }

    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateNewUsersMail(token, request);
        model.addAttribute("message", "Email address changes successfully");
        return "redirect:/admin";
    }
}
