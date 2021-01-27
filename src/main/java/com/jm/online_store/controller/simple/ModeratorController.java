package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.ReportCommentService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/moderator")
@AllArgsConstructor
public class ModeratorController {
    private final UserService userService;
    private final ReportCommentService reportCommentService;

    @GetMapping
    public String moderatorPage(Model model) {
        model.addAttribute("user", userService.getCurrentLoggedInUser());
        return "moderator-page";
    }

    @PostMapping("/profile")
    public String updateUserProfile(User user, Model model) {
        User updateUser = userService.updateUserProfile(user);
        model.addAttribute("user", updateUser);
        return "redirect:/moderator";
    }

}
