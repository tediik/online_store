package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * класс тестирования для методов FavouriteGoodsService
 */
public class FavouriteGoodsServiceImplTest {
    @Mock
    UserService userService;
    @Mock
    ProductService productService;
    @InjectMocks
    FavouriteGoodsServiceImpl favouriteGoodsService;

    Set<Product> favouriteGoods;
    User user;
    Product product1;
    Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        favouriteGoods = new HashSet<>();
        product1 = new Product("product1",220,10);
        product2 = new Product("product2", 300, 1);
        user = new User("user@mail.ru","1");
        favouriteGoods.add(product1);
        favouriteGoods.add(product2);
        user.setFavouritesGoods(favouriteGoods);
    }

    @AfterEach
    void tearDown() {
        product1 = null;
        product2 = null;
        user = null;
        favouriteGoods = null;
    }

    /**
     * метод тестирования получения избранных товаров
     */
    @Test
    public void getFavouriteGoodsTest() {
        when(userService.findById(any())).thenReturn(Optional.of(user));
        assertEquals(favouriteGoodsService.getFavouriteGoods(user),user.getFavouritesGoods());
    }

    /**
     * тест того, что метод добавления товара в избранное бросает ислючение
     */
    @Test
    public void addFavouriteGoodsThrowsExceptions() {
        when(userService.findById(any())).thenReturn(Optional.empty());
        when(productService.findProductById(any())).thenReturn(Optional.of(product1));
        assertThrows(UserNotFoundException.class,() -> favouriteGoodsService.addToFavouriteGoods(any(),user));
        verify(userService,times(1)).findById(any());

        when(userService.findById(any())).thenReturn(Optional.of(user));
        when(productService.findProductById(any())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,() -> favouriteGoodsService.addToFavouriteGoods(any(),user));
        verify(productService,times(1)).findProductById(any());
    }

    /**
     * тест того, что метод удаления товара из избранного бросает ислючение
     */
    @Test
    public void deleteFavouriteGoodsThrowsExceptions() {
        when(userService.findById(any())).thenReturn(Optional.empty());
        when(productService.findProductById(any())).thenReturn(Optional.of(product1));
        assertThrows(UserNotFoundException.class,() -> favouriteGoodsService.deleteFromFavouriteGoods(any(),user));
        verify(userService,times(1)).findById(any());

        when(userService.findById(any())).thenReturn(Optional.of(user));
        when(productService.findProductById(any())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,() -> favouriteGoodsService.deleteFromFavouriteGoods(any(),user));
        verify(productService,times(1)).findProductById(any());
    }

    /**
     * тест того, что метод получения избранных товаров бросает исключение
     */
    @Test
    public void getFavouriteGoodsThrowsException() {
        when(userService.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,() -> favouriteGoodsService.getFavouriteGoods(user));
    }

}