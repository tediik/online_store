package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Рест контроллер для избранных товаров.
 */
@AllArgsConstructor
@RestController
public class FavouritesGoodsRestController {
    private final UserService userService;

    /**
     * контроллер для получения товаров "избранное" для авторизованного User.
     * используется поиск по идентификатору User, т.к. используется ленивая
     * загрузка товаров, добавленных в "избранное".
     *
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return ResponseEntity<> список избранных товаров данного User + статус ответа.
     */
    @GetMapping(value = "/customer/favouritesGoods")
    public ResponseEntity<Set<Product>> getFavouritesGoods(Authentication authentication) {
        User autorityUser = userService.findById(((User) authentication.getPrincipal()).getId()).get();
        Set<Product> favouritesGoods = autorityUser.getFavouritesGoods();
        return new ResponseEntity<>(favouritesGoods, HttpStatus.OK);
    }
}
