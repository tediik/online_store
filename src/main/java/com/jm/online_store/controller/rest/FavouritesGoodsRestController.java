package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.FavouriteGoodsService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Рест контроллер для избранных товаров.
 */
@AllArgsConstructor
@RestController
@Api(value = "Rest Controller for favourite products")
@RequestMapping("/api/customer")
public class FavouritesGoodsRestController {
    private final FavouriteGoodsService favouriteGoodsService;
    private final UserService userService;
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;
    private final CustomerService customerService;

    /**
     * контроллер для получения товаров "избранное" для авторизованного User.
     * используется поиск по идентификатору User, т.к. используется ленивая
     * загрузка товаров, добавленных в "избранное".
     *
     * @return ResponseEntity<> список избранных товаров данного User + статус ответа.
     */
    @GetMapping(value = "/favouritesGoods")
    @ApiOperation(value = "Rest Controller fetches products from Favourite products for current logged in User",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<Set<Product>> getFavouritesGoods() {
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(favouriteGoodsService.getFavouriteGoods(user));
    }

    /**
     * Контроллер добавления товара в избранное.
     * Добавляем продукт в список избранного "Все товары"
     * @param id идентификатор товара
     * @return ResponseEntity.ok()
     */
    @PutMapping(value = "/favouritesGoods")
    @ApiOperation(value = "Rest Controller adds products to favourites. Adds it to the list \"All products\" ",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity addFavouritesGoods(@RequestBody Long id) {
        Customer customer = customerService.getCurrentLoggedInCustomer();
        favouriteGoodsService.addToFavouriteGoods(id, customer);
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        FavouritesGroup favouritesGroup = favouritesGroupService.getOneFavouritesGroupByUserAndByName(customer, "Все товары");
        favouritesGroupService.addProductToFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok().build();
    }

    /**
     * Контроллер удаления товара из избранного списка товаров.
     * Удаляем продукт из списка "Все товары"
     * @param id идентификатор товара
     * @return ResponseEntity.ok()
     */
    @DeleteMapping(value = "/favouritesGoods")
    @ApiOperation(value = "Rest Controller deletes product from favourites. From the list \"All products\" ",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity deleteFromFavouritesGoods(@RequestBody Long id) {
        Customer customer = customerService.getCurrentLoggedInCustomer();
        favouriteGoodsService.deleteFromFavouriteGoods(id, customer);
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        FavouritesGroup favouritesGroup = favouritesGroupService.getOneFavouritesGroupByUserAndByName(customer, "Все товары");
        favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({UserNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}
