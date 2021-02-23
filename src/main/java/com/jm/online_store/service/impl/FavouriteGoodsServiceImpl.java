package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CustomerService;
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
    private final CustomerService customerService;
    /**
     * метод удаления продукта из списка избранного
     * @param id продукта
     * @param currentUser авторизованный user
     * @throws {@link UserNotFoundException},{@link ProductNotFoundException}
     */
    @Override
    public void deleteFromFavouriteGoods(Long id, User currentUser) {
        Customer customer = customerService.findById(currentUser.getId()).orElseThrow(()
                -> new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
        Product productToDelete = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        Set<Product> favouriteGoods = customer.getFavouritesGoods();
        if(favouriteGoods.contains(productToDelete)) {
            favouriteGoods.remove(productToDelete);
        }
        customer.setFavouritesGoods(favouriteGoods);
        userService.updateUser(currentUser);
    }

    /**
     * метод добавления товара в избранное
     * @param id продукта
     * @param currentUser авторизованный user
     * @throws {@link UserNotFoundException},{@link ProductNotFoundException}
     */
    @Override
    public void addToFavouriteGoods(Long id, User currentUser) {
        Customer customer = customerService.findById(currentUser.getId()).orElseThrow(()
                -> new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        Set<Product> favouritesGoods = customer.getFavouritesGoods();
        favouritesGoods.add(product);
        customer.setFavouritesGoods(favouritesGoods);
        userService.updateUser(customer);
    }

    /**
     * метод получения всем товаров из избранного для конкретного пользователя
     * @param currentUser авторизованный user
     * @throws {@link UserNotFoundException}
     */
    @Override
    public Set<Product> getFavouriteGoods(User currentUser) {
        Customer customer = customerService.findById(currentUser.getId()).orElseThrow(()
                -> new CustomerNotFoundException(ExceptionEnums.CUSTOMER.getText() + ExceptionConstants.NOT_FOUND));
        return customer.getFavouritesGoods();
    }
}
