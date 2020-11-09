package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.OrderDTO;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * метод получения списка всех заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getAllOrders")
    public ResponseEntity<Set<Order>> getAllOrders() {
        User autorityUser = userService.getCurrentLoggedInUser();
        Set<Order> orders = autorityUser.getOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения списка всех действительных заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getIncartsOrders")
    public ResponseEntity<List<Order>> getIncartsOrders() {
        User autorityUser = userService.getCurrentLoggedInUser();
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.INCARTS);
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения списка всех завершенных заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getCompletedOrders")
    public ResponseEntity<List<Order>> getCompletedOrders() {
        User autorityUser = userService.getCurrentLoggedInUser();
        List<Order> orders = orderService.findAllByUserIdAndStatus(autorityUser.getId(), Order.Status.COMPLETED);
        return ResponseEntity.ok(orders);
    }

    /**
     * метод получения списка всех отмененных заказов авторизованного пользователя.
     * @return список заказов.
     */
    @GetMapping("/getCanceledOrders")
    public ResponseEntity<List<Order>> getCanceledOrders() {
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
