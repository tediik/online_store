package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Рест контроллер для избранных товаров.
 */
@RestController
public class FavouritesGoodsRestController {
    private final UserService userService;

    @Autowired
    public FavouritesGoodsRestController(UserService userService) {
        this.userService = userService;
    }
    /**
     * контроллер для получения товаров избранное для авторизованного пользователяю
     * используется поиск по идентификатору пользователя, т.к. используется ленивая под
     * грузка товаров, добавленных в избранное.
     * @param authentication залогированныц пользователь.
     * @return ResponseEntity<> список избранных товаров данного пользователя + статус ответа.
     */
    @GetMapping(value = "/customer/favouritesGoods")
    public ResponseEntity<Set<Product>> getFavouritesGoods(Authentication authentication) {
        User autorityUser = userService.findById(((User)authentication.getPrincipal()).getId()).get();
        Set<Product> favouritesGoods = autorityUser.getFavouritesGoods();
        return new ResponseEntity<>(favouritesGoods, HttpStatus.OK);
    }
}
