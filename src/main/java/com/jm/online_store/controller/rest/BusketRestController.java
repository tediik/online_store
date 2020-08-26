package com.jm.online_store.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.model.Basket;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Рест контроллер для корзины.
 */
@AllArgsConstructor
@RestController
public class BusketRestController {
    private final UserService userService;
    private final BasketService basketService;
    private final OrderService orderService;
    private final ProductInOrderService productInOrderService;
    private final ProductService productService;

    /**
     * мтеод для получения авторизованного пользователя.
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return авторизванный пользователь.
     */
    private User getAutorityUser(Authentication authentication) {
        return userService.findById(((User) authentication.getPrincipal()).getId()).get();
    }

    /**
     * контроллер для получения товаров в корзине для авторизованного User.
     * используется поиск по идентификатору User, т.к. используется ленивая
     * загрузка товаров, добавленных в "избранное".
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return ResponseEntity<> список избранных товаров данного User + статус ответа.
     */
    @GetMapping(value = "/customer/busketGoods")
    public ResponseEntity<List<Basket>> getBasket(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<Basket> baskets = autorityUser.getUserBasket();
        return new ResponseEntity<>(baskets, HttpStatus.OK);
    }

    /**
     * контроллер для формирования заказа из корзины.
     *
     * @param authentication авторизованный пользователь.
     * @return ResponseEntity(HttpStatus.OK)
     */
    @PostMapping(value = "/customer/busketGoods")
    public ResponseEntity buildOrderFromBasket(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<Basket> basketList = autorityUser.getUserBasket();
        Product product;
        int count = 0;
        double sum = 0;
        Order order = new Order();
        long orderId = orderService.addOrder(order);
        for (Basket basket : basketList) {
            productInOrderService.addToOrder(basket.getProduct().getId(), orderId, basket.getCount());
            product = basket.getProduct();
            product.setAmount(product.getAmount() - basket.getCount());
            productService.saveProduct(product);
            count += basket.getCount();
            sum += basket.getProduct().getPrice() * basket.getCount();
        }
        order.setDateTime(LocalDateTime.now());
        order.setAmount((long) count);
        order.setOrderPrice(sum);
        order.setStatus(Order.Status.INCARTS);
        Set<Order> orderSet = autorityUser.getOrders();
        orderSet.add(order);
        autorityUser.setOrders(orderSet);
        orderService.updateOrder(order);
        autorityUser.setUserBasket(new ArrayList<>());
        userService.updateUser(autorityUser);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Контроллер для удаления сущности Basket (корзина) из списка корзин User.
     *
     * @param id             идентификатор миникорзины
     * @param authentication авторизованный пользователь User
     * @return ResponseEntity(HttpStatus.OK)
     */
    @DeleteMapping(value = "/customer/busketGoods")
    public ResponseEntity deleteBasket(@RequestBody Long id, Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<Basket> basketList = autorityUser.getUserBasket();
        basketList.remove(basketService.findBasketById(id));
        autorityUser.setUserBasket(basketList);
        userService.updateUser(autorityUser);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     *
     * @param json
     * @return
     */
    @PutMapping(value = "/customer/busketGoods")
    public ResponseEntity updateUpBasket(@RequestBody ObjectNode json) {
        Long id = json.get("id").asLong();
        int difference = json.get("count").asInt();
        Basket basket = basketService.findBasketById(id);
        int count = basket.getCount();
        if (difference > 0) {
            if (basket.getProduct().getAmount() > count) {
                count += difference;
                basket.setCount(count);
            } else {
                basket.setCount(basket.getProduct().getAmount());
            }
        } else {
            if (count > 1) {
                count += difference;
                basket.setCount(count);
            }
        }
            basketService.updateBasket(basket);
        return new ResponseEntity(HttpStatus.OK);
    }
}
