package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouriteGoodsService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class FavouriteGoodsServiceImpl implements FavouriteGoodsService {
    private final UserService userService;
    private final ProductService productService;

    /**
     * метод удаления продукта из списка избранного
     * @param id продукта
     * @param currentUser авторизованный user
     * @throws {@link UserNotFoundException},{@link ProductNotFoundException}
     */
    @Override
    public void deleteFromFavouriteGoods(Long id, User currentUser) {
//        User user = userService.findById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
        User user = userService.getCurrentLoggedInUser();
        Product productToDelete = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        Set<Product> favouriteGoods = user.getFavouritesGoods();
        if(favouriteGoods.contains(productToDelete)) {
            favouriteGoods.remove(productToDelete);
        }
        user.setFavouritesGoods(favouriteGoods);
        userService.updateUser(user);
    }

    /**
     * метод добавления товара в избранное
     * @param id продукта
     * @param currentUser авторизованный user
     * @throws {@link UserNotFoundException},{@link ProductNotFoundException}
     */
    @Override
    public void addToFavouriteGoods(Long id, User currentUser) {
//        User user = userService.findById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
        User user = userService.getCurrentLoggedInUser();
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        Set<Product> favouritesGoods = user.getFavouritesGoods();
        favouritesGoods.add(product);
        user.setFavouritesGoods(favouritesGoods);
        userService.updateUser(user);
    }

    /**
     * метод получения всем товаров из избранного для конкретного пользователя
     * @param currentUser авторизованный user
     * @throws {@link UserNotFoundException}
     */
    @Override
    public Set<Product> getFavouriteGoods(User currentUser) {
//        User user = userService.findById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
        User user = userService.getCurrentLoggedInUser();
        return user.getFavouritesGoods();
    }
}