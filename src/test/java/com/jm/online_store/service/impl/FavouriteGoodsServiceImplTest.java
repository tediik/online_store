package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import org.checkerframework.checker.units.qual.C;
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

    @Mock
    CustomerService customerService;

    @InjectMocks
    FavouriteGoodsServiceImpl favouriteGoodsService;

    Set<Product> favouriteGoods;
    Customer customer;
    Product product1;
    Product product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        favouriteGoods = new HashSet<>();
        product1 = new Product("product1",220D,10);
        product2 = new Product("product2", 300D, 1);
        customer = new Customer("user@mail.ru","1");
        favouriteGoods.add(product1);
        favouriteGoods.add(product2);
        customer.setFavouritesGoods(favouriteGoods);
    }

    @AfterEach
    void tearDown() {
        product1 = null;
        product2 = null;
        customer = null;
        favouriteGoods = null;
    }

    /**
     * метод тестирования получения избранных товаров
     */
    @Test
    public void getFavouriteGoodsTest() {
        when(customerService.findById(any())).thenReturn(Optional.of(customer));
        assertEquals(favouriteGoodsService.getFavouriteGoods(customer), customer.getFavouritesGoods());
    }

    /**
     * Тест метода добавления товара в избранное
     */
    @Test
    public void addFavouriteGoods() {
        when(customerService.findById(any())).thenReturn(Optional.of(customer));
        when(productService.findProductById(any())).thenReturn(Optional.of(product1));
        Product testProduct = new Product("productTest", 500D, 10);
        favouriteGoodsService.addToFavouriteGoods(3L, customer);
        verify(userService,times(1)).findById(any());
        verify(productService,times(1)).findProductById(any());
        favouriteGoods.add(testProduct);
        assertEquals(customer.getFavouritesGoods(),favouriteGoods);
    }

    /**
     * тест метода удаления товара из избранного
     */
    @Test
    public void deleteFavouriteGoods() {
        when(customerService.findById(any())).thenReturn(Optional.of(customer));
        when(productService.findProductById(any())).thenReturn(Optional.of(product1));
        favouriteGoodsService.deleteFromFavouriteGoods(1L, customer);
        verify(userService,times(1)).findById(any());
        verify(productService,times(1)).findProductById(any());
        favouriteGoods.remove(product1);
        assertEquals(favouriteGoods, customer.getFavouritesGoods());
    }

    /**
     * тест того, что метод добавления товара в избранное бросает исключение
     */
    @Test
    public void addFavouriteGoodsThrowsExceptions() {
        when(customerService.findById(any())).thenReturn(Optional.empty());
        when(productService.findProductById(any())).thenReturn(Optional.of(product1));
        assertThrows(UserNotFoundException.class,() -> favouriteGoodsService.addToFavouriteGoods(any(), customer));

        when(userService.findById(any())).thenReturn(Optional.of(customer));
        when(productService.findProductById(any())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,() -> favouriteGoodsService.addToFavouriteGoods(any(), customer));
    }

    /**
     * тест того, что метод удаления товара из избранного бросает исключение
     */
    @Test
    public void deleteFavouriteGoodsThrowsExceptions() {
        when(customerService.findById(any())).thenReturn(Optional.empty());
        when(productService.findProductById(any())).thenReturn(Optional.of(product1));
        assertThrows(UserNotFoundException.class,() -> favouriteGoodsService.deleteFromFavouriteGoods(any(), customer));
        verify(customerService,times(1)).findById(any());

        when(customerService.findById(any())).thenReturn(Optional.of(customer));
        when(productService.findProductById(any())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,() -> favouriteGoodsService.deleteFromFavouriteGoods(any(), customer));
        verify(productService,times(1)).findProductById(any());
    }

    /**
     * тест того, что метод получения избранных товаров бросает исключение
     */
    @Test
    public void getFavouriteGoodsThrowsException() {
        when(customerService.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,() -> favouriteGoodsService.getFavouriteGoods(customer));
    }
}
