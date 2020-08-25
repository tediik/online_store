package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping("/uploadImage/{id}")
    public String handleImagePost(@PathVariable String id, @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        userService.updateUserImage(Long.valueOf(id), imageFile);
        return "redirect:/customer/profile";
    }

    @PostMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable Long id) throws IOException {
        userService.deleteUserImage(id);
        return "redirect:/customer/profile";
    }

    @GetMapping("/order")
    public String getOrderPage(Authentication auth, Model model) {
        Long userId = ((User) auth.getPrincipal()).getId();

        model.addAttribute("orders", List.copyOf(orderService.findAllByUserId(userId)));
        model.addAttribute("incartsOrders", List.copyOf(orderService.findAllByUserIdAndStatus(userId, Order.Status.INCARTS)));
        model.addAttribute("completedOrders", List.copyOf(orderService.findAllByUserIdAndStatus(userId, Order.Status.COMPLETED)));
        model.addAttribute("canceledOrders", List.copyOf(orderService.findAllByUserIdAndStatus(userId, Order.Status.CANCELED)));

        return "customerOrder";
    }

    @GetMapping("/order/{id}")
    public String orderDetails(@PathVariable(value = "id") Long id, Model model) {
        Order order = orderService.findOrderById(id).orElseGet(Order::new);
        model.addAttribute("order", order);
        return "customerOrderDetails";
    }
}
