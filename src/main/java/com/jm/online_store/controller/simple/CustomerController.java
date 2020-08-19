package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.service.OrderService;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
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
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final OrderService orderService;

    @GetMapping
    public String getCustomerPage() {
        return "customerPage";
    }

    @GetMapping("/profile")
    public String getPersonalInfo(Model model, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        User user = userService.findById(principal.getId()).get();
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateUserInfo(User user, Model model) {
        user.setRoles(Collections.singleton(roleService.findByName("ROLE_CUSTOMER").get()));
        userService.updateUser(user);
        model.addAttribute("user", user);

        return "/profile";
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
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("message", "Неверный старый пароль!");

            return "changePassword";
        }
        user.setPassword(newPassword);
        userService.updateUser(user);

        return "redirect:/customer/profile";
    }

    @GetMapping("/order")
    public String getOrderPage(Authentication auth, Model model) {
        User principal = (User) auth.getPrincipal();

//        List<Order> orders = orderService.findByUserId(principal.getId());
        List<Order> orders = orderService.findAll();

//        List<Order> incartsOrders = orderService.findAllByStatusEquals("INCARTS");
//        List<Order> completedOrders = orderService.findAllByStatusEquals("COMPLETED");
//        List<Order> canceledOrders = orderService.findAllByStatusEquals("CANCELED");

        model.addAttribute("orders", orders);
//        model.addAttribute("incartsOrders", incartsOrders);
//        model.addAttribute("completedOrders", completedOrders);
//        model.addAttribute("canceledOrders", canceledOrders);

        return "customerOrder";
    }

    @GetMapping("/wishlist")
    public String getWishList() {
        return "customerWishlist";
    }
}
