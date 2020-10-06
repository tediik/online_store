package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouriteGoodsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Рест контроллер для избранных товаров.
 */
@AllArgsConstructor
@RestController
public class FavouritesGoodsRestController {
    FavouriteGoodsService favouriteGoodsService;
    
    /**
     * контроллер для получения товаров "избранное" для авторизованного User.
     * используется поиск по идентификатору User, т.к. используется ленивая
     * загрузка товаров, добавленных в "избранное".
     *
     * @param user модель данных, построенная на основе залогированного User.
     * @return ResponseEntity<> список избранных товаров данного User + статус ответа.
     */
    @GetMapping(value = "/customer/favouritesGoods")
    public ResponseEntity<Set<Product>> getFavouritesGoods(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(favouriteGoodsService.getFavouriteGoods(user));
    }

    /**
     *контроллер добавления товара в избранное.
     *
     * @param id идентификатор товара
     * @param user модель данных, построенная на основе залогированного User.
     * @return ResponseEntity.ok()
     */
    @PutMapping(value = "/customer/favouritesGoods")
    public ResponseEntity addFavouritesGoods(@RequestBody Long id, @AuthenticationPrincipal User user) {
        favouriteGoodsService.addToFavouriteGoods(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     *контроллер удаления товара из избранного списка товаров.
     *
     * @param id идентификатор товара
     * @param user модель данных, построенная на основе залогированного User.
     * @return ResponseEntity.ok()
     */
    @DeleteMapping(value = "/customer/favouritesGoods")
    public ResponseEntity deleteFromFavouritesGoods(@RequestBody Long id, @AuthenticationPrincipal User user) {
        favouriteGoodsService.deleteFromFavouriteGoods(id, user);
        return ResponseEntity.ok().build();
    }
    
    @ExceptionHandler({UserNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}