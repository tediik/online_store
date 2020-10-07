package com.jm.online_store.controller.simple;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/")
public class MainPageController {
    private UserService userService;
    private final ConfirmationTokenRepository confirmTokenRepository;

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
        } catch (EmailAlreadyExistsException ex) {
            ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
            User user = userService.getUserByToken(token);
            model.addAttribute("user", user);
            try {
                request.login(user.getEmail(), confirmationToken.getUserPassword());
            } catch (ServletException e) {
                log.debug("Servlet exception from ActivateUser Method {}", e.getMessage());
            }
            return "redirect:/customer";
        }
        User user = userService.getUserByToken(token);
        model.addAttribute("message", "User email address confirmed successfully");
        model.addAttribute("user", user);
        return "redirect:/customer";
    }

}
