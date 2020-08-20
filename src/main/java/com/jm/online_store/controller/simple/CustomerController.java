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
import java.util.Optional;

/**
 * CustomerController контроллер для пользователя с ролью "Customer"
 */
@AllArgsConstructor
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    /**
     * метод получения данных зарегестрированного пользователя.
     * формирование модели для вывода в "view"
     * @param model модель для view
     * @param auth модель данных, построенных на основе зарегестрированного User
     * @return
     */
    @GetMapping
    public String getPersonalInfo(Model model, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        User user = userService.findById(principal.getId()).get();
        model.addAttribute("user", user);

        return "customerPage";
    }

    /**
     * метод ля формирования данных для обновления User.
     * @param user пользователь
     * @param model модель для view
     * @return
     */
    @PostMapping("/profile")
    public String updateUserInfo(User user, Model model) {
        user.setRoles(Collections.singleton(roleService.findByName("ROLE_CUSTOMER").get()));
       User updadeUser = userService.findById(user.getId()).get();
        updadeUser.setFirstName(user.getFirstName());
        updadeUser.setLastName(user.getLastName());
        updadeUser.setEmail(user.getEmail());
        updadeUser.setBirthdayDate(user.getBirthdayDate());
        updadeUser.setUserGender(user.getUserGender());
        userService.updateUser(updadeUser);
        model.addAttribute("user", updadeUser);

        return "customerPage";
    }

    /**
     * метод для вызова страницы изменения пароля User.
     * @return страница для изменения данных.
     */
    @GetMapping("/change-password")
    public String changePassword() {

        return "changePassword";
    }

    /**
     * метод обработки изменения пароля User.
     * @param auth модель данных, построенных на основе зарегестрированного User
     * @param model модель для view
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return страница User
     */
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

        return "customerPage";
    }
}
