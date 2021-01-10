package com.jm.online_store.controller.simple;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
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
    private final UserService userService;
    private final ConfirmationTokenRepository confirmTokenRepository;

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("userForm", new User());
        return "main-page";
    }

    /**
     * Method gets confirmation token after registering mail and returns customer page     *
     *
     * @param model   to create view
     * @param token   to find new registered user by token
     * @param request parameters to transfer into UserService.activateUser method
     * @return customer page with newly registered user
     */
    @GetMapping("/activate/{token}")
    public String registerMail(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateUser(token, request);
        User user = userService.getUserByToken(token);
        model.addAttribute("message", "User email address confirmed successfully");
        model.addAttribute("user", user);
        return "redirect:/customer";
    }

    /**
     * метод обработки ссылки-подтверждения для смены логина покупателя.
     * После перехода по ссылке запрос на изменение логина одобряется и происходит его обновление
     */
    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request) {
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        userService.activateNewUsersMail(token, request);
        User user = userService.findById(confirmationToken.getUserId()).orElseThrow(UserNotFoundException::new);
        model.addAttribute("message", "User email address confirmed successfully");
        model.addAttribute("user", user);
        return "redirect:/logout";
    }

    /**
     * Метод отрабатывает при переходе по ссылке-подтверждению полученной на почту при запросе на сброс и генерацию нового пароля
     */
    @GetMapping("/restorepassword/{token}")
    public String restorePassword(Model model, @PathVariable String token) {
        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        User user = userService.findById(confirmationToken.getUserId()).orElseThrow(UserNotFoundException::new);
        userService.restorePassword(user);
        model.addAttribute("message", "User password has been restored");
        model.addAttribute("user", user);
        return "redirect:/login";
    }
}
