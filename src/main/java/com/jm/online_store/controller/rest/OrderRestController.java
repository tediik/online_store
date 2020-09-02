package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.ProductInOrder;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
/**
 * Рест контроллер для заказов.
 */
@AllArgsConstructor
@RestController
public class OrderRestController {
    private final UserService userService;
    private final OrderService orderService;
    private final ProductInOrderService productInOrderService;
    private final ProductService productService;
    /**
     * метод для получения авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return авторизванный пользователь.
     */
    private User getAutorityUser(Authentication authentication) {
        return userService.findById(((User) authentication.getPrincipal()).getId()).get();
    }
    @GetMapping("/customer/getAllOrders")
    public ResponseEntity<Set<Order>> getAllOrders(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        Set<Order> orders = autorityUser.getOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/getIncartsOrders")
    public ResponseEntity<List<Order>> getIncartsOrders(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.INCARTS);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/getCompletedOrders")
    public ResponseEntity<List<Order>> getCompletedOrders(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.COMPLETED);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/getCanceledOrders")
    public ResponseEntity<List<Order>> getCanceledOrders(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.CANCELED);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/customer/getOrderById")
    public ResponseEntity<OrderDTO> getOrderById(@RequestBody Long id) {
        OrderDTO order = orderService.findOrderDTOById(id);
        return ResponseEntity.ok(order);
    }
}
