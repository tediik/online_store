package com.jm.online_store.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.AddressService;
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
    private final AddressService addressService;

    /**
     * метод для получения авторизованного пользователя.
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
    public ResponseEntity<List<SubBasket>> getBasket(Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<SubBasket> subBaskets = autorityUser.getUserBasket();
        int productCount;
        for (SubBasket subBasket : subBaskets) {
            productCount = productService.findProductById(subBasket.getProduct().getId()).get().getAmount();
            if (productCount < subBasket.getCount()) {
                subBasket.setCount(productCount);
                if (productCount < 1) {
                    subBaskets.remove(subBasket);
                    basketService.deleteBasket(subBasket);
                }
                autorityUser.setUserBasket(subBaskets);
                userService.updateUser(autorityUser);
            }
        }
        return new ResponseEntity<>(subBaskets, HttpStatus.OK);
    }

    /**
     * контроллер для формирования заказа из корзины.
     *
     * @param authentication авторизованный пользователь.
     * @return ResponseEntity(HttpStatus.OK)
     */
    @PostMapping(value = "/customer/busketGoods")
    public ResponseEntity buildOrderFromBasket(Authentication authentication, @RequestBody Address address) {
        Address addressToAdd;
        if (address.isShop()) {
            addressToAdd = addressService.findAddressById(address.getId()).orElseThrow(AddressNotFoundException::new);
        } else {
            addressToAdd = addressService.addAddress(address);
        }
        User autorityUser = getAutorityUser(authentication);
        List<SubBasket> subBasketList = autorityUser.getUserBasket();
        Product product;
        int count = 0;
        double sum = 0;
        Order order = new Order();
        long orderId = orderService.addOrder(order);
        for (SubBasket subBasket : subBasketList) {
            productInOrderService.addToOrder(subBasket.getProduct().getId(), orderId, subBasket.getCount());
            product = subBasket.getProduct();
            product.setAmount(product.getAmount() - subBasket.getCount());
            productService.saveProduct(product);
            count += subBasket.getCount();
            sum += subBasket.getProduct().getPrice() * subBasket.getCount();
        }
        order.setDateTime(LocalDateTime.now());
        order.setAmount((long) count);
        order.setOrderPrice(sum);
        order.setStatus(Order.Status.INCARTS);
        order.setAddress(addressService.findAddressById(addressToAdd.getId()).get());
        Set<Order> orderSet = autorityUser.getOrders();
        orderSet.add(order);
        autorityUser.setOrders(orderSet);
        orderService.updateOrder(order);
        autorityUser.setUserBasket(new ArrayList<>());
        userService.updateUser(autorityUser);
        return ResponseEntity.ok().build();
    }

    /**
     * Контроллер для удаления сущности SubBasket (корзина) из списка корзин User.
     *
     * @param id идентификатор миникорзины
     * @param authentication авторизованный пользователь User
     * @return ResponseEntity(HttpStatus.OK)
     */
    @DeleteMapping(value = "/customer/busketGoods")
    public ResponseEntity deleteBasket(@RequestBody Long id, Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        List<SubBasket> subBasketList = autorityUser.getUserBasket();
        subBasketList.remove(basketService.findBasketById(id));
        autorityUser.setUserBasket(subBasketList);
        userService.updateUser(autorityUser);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * контроллер для обновления количества товара в корзине.
     *
     * @param json json из 2-х параметров
     * @return ResponseEntity(HttpStatus.OK)
     */
    @PutMapping(value = "/customer/busketGoods")
    public ResponseEntity updateUpBasket(@RequestBody ObjectNode json) {
        Long id = json.get("id").asLong();
        int difference = json.get("count").asInt();
        SubBasket subBasket = basketService.findBasketById(id);
        int count = subBasket.getCount();
        if (difference > 0) {
            if (subBasket.getProduct().getAmount() > count) {
                count += difference;
                subBasket.setCount(count);
            } else {
                subBasket.setCount(subBasket.getProduct().getAmount());
            }
        } else {
            if (count > 1) {
                count += difference;
                subBasket.setCount(count);
            }
        }
        basketService.updateBasket(subBasket);
        return new ResponseEntity(HttpStatus.OK);
    }
}
