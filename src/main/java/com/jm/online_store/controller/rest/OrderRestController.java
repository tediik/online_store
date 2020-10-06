package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * Рест контроллер для заказов.
 */
@AllArgsConstructor
@RequestMapping("/customer")
@RestController
public class OrderRestController {
    private final UserService userService;
    private final OrderService orderService;

    /**
     * метод для получения авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return авторизванный пользователь.
     */
//    private User getAutorityUser(Authentication authentication) {
////        return userService.findByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new);
//        return userService.getCurrentLoggedInUser();
//    }

    /**
     * метод получения списка всех заказов авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return список заказов.
     */
    @GetMapping("/getAllOrders")
    public ResponseEntity<Set<Order>> getAllOrders(Authentication authentication) {
//        User autorityUser = getAutorityUser(authentication);
        User autorityUser = userService.getCurrentLoggedInUser();
        Set<Order> orders = autorityUser.getOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения списка всех действительных заказов авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return список заказов.
     */
    @GetMapping("/getIncartsOrders")
    public ResponseEntity<List<Order>> getIncartsOrders(Authentication authentication) {
        User autorityUser = userService.getCurrentLoggedInUser();
//        User autorityUser = getAutorityUser(authentication);
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.INCARTS);
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения списка всех завершенных заказов авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return список заказов.
     */
    @GetMapping("/getCompletedOrders")
    public ResponseEntity<List<Order>> getCompletedOrders(Authentication authentication) {
//        User autorityUser = getAutorityUser(authentication);
        User autorityUser = userService.getCurrentLoggedInUser();
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.COMPLETED);
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения списка всех отмененных заказов авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return список заказов.
     */
    @GetMapping("/getCanceledOrders")
    public ResponseEntity<List<Order>> getCanceledOrders(Authentication authentication) {
//        User autorityUser = getAutorityUser(authentication);
        User autorityUser = userService.getCurrentLoggedInUser();
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.CANCELED);
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения данных по заказу по идентификатору.
     *
     * @param id идентификатор заказа
     * @return данные заказа по DTO
     */
    @PostMapping("/getOrderById")
    public ResponseEntity<OrderDTO> getOrderById(@RequestBody Long id) {
        OrderDTO order = orderService.findOrderDTOById(id);
        return ResponseEntity.ok(order);
    }

}
