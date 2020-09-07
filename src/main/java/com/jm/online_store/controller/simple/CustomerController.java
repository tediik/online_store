package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
     * @return
     */
    @GetMapping
    public String getPersonalInfo(Model model, Authentication auth) {
        User user = (User) auth.getPrincipal();
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
        updadeUser.setBirthdayDate(user.getBirthdayDate());
        updadeUser.setUserGender(user.getUserGender());
        userService.updateUser(updadeUser);
        model.addAttribute("user", updadeUser);

        return "customerPage";
    }

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
            model.addAttribute("message", "Pls, double check previous password!");

            return "redirect:/customer/profile" ;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);

        return "customerPage";
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

    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request){
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
