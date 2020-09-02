package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final UserService userService;
    private final ProductService productService;

    /**
     *метод получения авторизованного пользователя.
     *
     * @param authentication модель данных построенная на основе авторизованного пользователя
     * @return объект авторизованного пользователя
     */
    private User getAutorityUser(Authentication authentication) {
        return userService.findById(((User) authentication.getPrincipal()).getId()).get();
    }

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
        User autorityUser = getAutorityUser(authentication);
        Set<Product> favouritesGoods = autorityUser.getFavouritesGoods();
        return ResponseEntity.ok(favouritesGoods);
    }

    /**
     *метод добавления товара в избранное.
     *
     * @param id идентификатор товара
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return ResponseEntity.ok()
     */
    @PutMapping(value = "/customer/favouritesGoods")
    public ResponseEntity addFavouritesGoods(@RequestBody Long id, Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        Product product = productService.findProductById(id).get();
        Set<Product> favouritesGoods = autorityUser.getFavouritesGoods();
        favouritesGoods.add(product);
        autorityUser.setFavouritesGoods(favouritesGoods);
        userService.updateUserFromController(autorityUser);
        return (ResponseEntity) ResponseEntity.ok();
    }

    /**
     *метод удаления товара из избранного списка товаров.
     *
     * @param id идентификатор товара
     * @param authentication модель данных, построенная на основе залогированного User.
     * @return ResponseEntity.ok()
     */
    @DeleteMapping(value = "/customer/favouritesGoods")
    public ResponseEntity deleteFromFavouritesGoods(@RequestBody Long id, Authentication authentication) {
        User autorityUser = getAutorityUser(authentication);
        Product product = productService.findProductById(id).get();
        Set<Product> favouritesGoods = autorityUser.getFavouritesGoods();
        if (favouritesGoods.contains(product)) {
            favouritesGoods.remove(product);
        }
        autorityUser.setFavouritesGoods(favouritesGoods);
        userService.updateUserFromController(autorityUser);
        return (ResponseEntity) ResponseEntity.ok();
    }
}
