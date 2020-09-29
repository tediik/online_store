package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
    private final RoleService roleService;
    private final OrderService orderService;

    /**
     * метод получения данных зарегестрированного пользователя.
     * формирование модели для вывода в "view"
     * модель данных, построенных на основе зарегестрированного User
     *
     * @return
     */
    @GetMapping
    public String getUserProfile(Model model, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        User user = userService.findById(principal.getId()).get();
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
     * @param auth        модель данных, построенных на основе зарегестрированного User
     * @param model       модель для view
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return страница User
     */
    @PostMapping("/change-password")
    public String changePassword(Authentication auth, Model model,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) {
        User user = (User) auth.getPrincipal();
        if (!userService.changePassword(user.getId(), oldPassword, newPassword)) {
            model.addAttribute("message", "Pls, check your old password!");
        }
        return "redirect:/customer";
    }

    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateNewUsersMail(token, request);
        model.addAttribute("message", "Email address changes successfully");
        return "redirect:/customer";
    }

    //временная заглушка пока нет акций на главной
    @GetMapping("/stockDetails")
    public String showStockDetailsPage(){
        return "stockDetailsPage";
    }
}
