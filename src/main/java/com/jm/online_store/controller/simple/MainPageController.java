package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/")
public class MainPageController {
    private UserService userService;

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("userForm", new User());
        return "mainPage";
    }

    /**
     *     Method gets confirmation token after registering mail and returns customer page     *
     * @param model to create view
     * @param token to find new registered user by token
     * @param request parameters to transfer into UserService.activateUser method
     * @return customer page with newly registered user
     */
    @GetMapping("/activate/{token}")
    public String registerMail(Model model, @PathVariable String token, HttpServletRequest request) {
        try {
            userService.activateUser(token, request);
        } catch (Exception ex) {
            log.debug("catch (Exception ex) {Ваш Email уже был подтвержден.}");
        }
        User user = userService.getUserByToken(token);
        model.addAttribute("message", "User email address confirmed successfully");
        model.addAttribute("user", user);
        return "redirect:/customer";
    }

}
