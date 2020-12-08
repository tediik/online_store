package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Customer;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CustomerService customerService;
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final FavouritesGroupService favouritesGroupService;

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
        Customer customer = customerService.getCurrentLoggedInUser();
        model.addAttribute("user", user);
        model.addAttribute("customer", customer);
        model.addAttribute("listOfComments", commentService.findAllByCustomer(user));
        model.addAttribute("favouritesGroupList", favouritesGroupService.findAllByUser(user));
        model.addAttribute("listOfReviews", reviewService.findAllByCustomer(user));
        return "customerPage";
    }

    /**
     * Связываем страницу корзины с адресом
     *
     * @return
     */
    @GetMapping("/basket")
    public String getUserBasket() {
        return "basketPage";
    }

    /**
     * метод для формирования данных для обновления User.
     *
     * @param user  пользователь
     * @param model модель для view
     * @return
     */
    @PostMapping("/profile")
    public String updateUserProfile(@RequestParam("dayOfWeekForStockSend") String dayOfWeekForStockSend, User user, Model model) {
        Customer customer = customerService.findById(user.getId()).get();
        customerService.updateCustomerDayOfWeekForStockSend(customer, dayOfWeekForStockSend);
        User updateUser = userService.updateUserProfile(user);
        model.addAttribute("user", updateUser);
        return "redirect:/customer";
    }
    // looks like useless
    @GetMapping("/change-password")
    public String changePassword() {
        return "changePassword";
    }
// looks like useless
    @GetMapping("/activatenewmail/{token}")
    public String changeMail(Model model, @PathVariable String token, HttpServletRequest request) {
        userService.activateNewUsersMail(token, request);
        model.addAttribute("message", "Email address changes successfully");
        return "redirect:/customer";
    }
}
