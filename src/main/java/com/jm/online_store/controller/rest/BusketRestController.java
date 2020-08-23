package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Basket;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер для корзины.
 */
@AllArgsConstructor
@RestController
public class BusketRestController {
    private final UserService userService;
    private final BasketService basketService;

    /**
     * контроллер для получения товаров в корзине для авторизованного User.
     * используется поиск по идентификатору User, т.к. используется ленивая
     * загрузка товаров, добавленных в "избранное".
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return ResponseEntity<> список избранных товаров данного User + статус ответа.
     */
    @GetMapping(value = "/customer/busketGoods")
    public ResponseEntity<List<Basket>> getFavouritesGoods(Authentication authentication) {
        User autorityUser = userService.findById(((User) authentication.getPrincipal()).getId()).get();
        List<Basket> baskets = autorityUser.getUserBasket();
        return new ResponseEntity<>(baskets, HttpStatus.OK);
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
        User autorityUser = userService.findById(((User) authentication.getPrincipal()).getId()).get();
        List<Basket> basketList = autorityUser.getUserBasket();
        basketList.remove(basketService.findBasketById(id));
        autorityUser.setUserBasket(basketList);
        userService.updateUser(autorityUser);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Контроллер для увеличения количества товара на 1.
     *
     * @param id идентификатор Basket
     * @return new ResponseEntity(HttpStatus.OK);
     */
    @PutMapping(value = "/customer/upBusketGoods")
    public ResponseEntity updateUpBasket(@RequestBody Long id) {
        Basket basket = basketService.findBasketById(id);
        int count = basket.getCount();
        if (basket.getProduct().getAmount() > count) {
            count = basket.getCount() + 1;
            basket.setCount(count);
            basketService.updateBasket(basket);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Контроллер для уменьшения количества товара на 1.
     *
     * @param id идентификатор Basket
     * @return new ResponseEntity(HttpStatus.OK);
     */
    @PutMapping(value = "/customer/downBusketGoods")
    public ResponseEntity updateDownBasket(@RequestBody Long id) {
        Basket basket = basketService.findBasketById(id);
        int count = basket.getCount();
        if (count > 1) {
            count = basket.getCount() - 1;
            basket.setCount(count);
            basketService.updateBasket(basket);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
