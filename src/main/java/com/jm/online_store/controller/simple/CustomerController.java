package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * CustomerController контроллер для пользователя с ролью "Customer"
 */
@AllArgsConstructor
@Controller
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final BCryptPasswordEncoder encoder;

    /**
     * метод получения данных зарегистрированного пользователя.
     * формирование модели для вывода в "view"
     * модель данных, построенных на основе зарегистрированного User
     *
     * @return
     */
    @GetMapping
    public String getUserProfile(Model model) {
        User user = userService.getCurrentLoggedInUser();
        model.addAttribute("user", user);
        return "customerPage";
    }

    /**
     * метод ля формирования данных для обновления User.
     *
     * @param user  пользователь
     * @param model модель для view
     * @return
     */
    @PostMapping("/profile")
    public String updateUserProfile(User user, Model model) {
        User updateUser = userService.updateUserProfile(user);
        model.addAttribute("user", updateUser);
        return "customerPage";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "changePassword";
    }

    /**
     * метод обработки изменения пароля User.
     *
     * @param model       модель для view
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return страница User
     */
    @PostMapping("/changepass")
    public ResponseEntity<String> changePassword(Model model,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        User user = userService.getCurrentLoggedInUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("message", "Неверный старый пароль!");
            return new ResponseEntity("error_old_pass", HttpStatus.BAD_REQUEST);
        }
        if (encoder.matches(oldPassword, user.getPassword())) {
            if (newPassword.length() < 8) {
                return new ResponseEntity("error_pass_len", HttpStatus.BAD_REQUEST);
            } else {
                if (!ValidationUtils.isValidPassword(newPassword)) {
                    return new ResponseEntity("error_valid", HttpStatus.BAD_REQUEST);
                } else {
                    if (userService.changePassword(user.getId(), oldPassword, newPassword)) {
                        log.debug("Пароль изменен!");
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userService.updateUser(user);
                    userService.changeUsersPass(user, user.getEmail());
                    return new ResponseEntity("ok", HttpStatus.OK);
                }
            }
        } else {
            return new ResponseEntity("error_old_pass", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateNewUsersMail(token, request);
        model.addAttribute("message", "Email address changes successfully");
        return "redirect:/customer";
    }
}
