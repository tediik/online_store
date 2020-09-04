package com.jm.online_store.service.impl;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductInOrder;
import com.jm.online_store.repository.ProductInOrderRepository;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductInOrderServiceImplTest {
    private final ProductService productService = Mockito.mock(ProductService.class);
    private final OrderService orderService = Mockito.mock(OrderService.class);
    private final ProductInOrderRepository productInOrderRepository = Mockito.mock(ProductInOrderRepository.class);
    private final ProductInOrderService productInOrderService =
            new ProductInOrderServiceImpl(productService, orderService, productInOrderRepository);

    @Test
    void addToOrder() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(6500d);
        product.setAmount(3);

        Order order = new Order();
        order.setId(1L);
        order.setOrderPrice((double) 0);
        order.setAmount(0L);

        double productPrice = product.getPrice();
        int productAmount = product.getAmount();
        double orderPrice = order.getOrderPrice();
        long orderAmount = order.getAmount();
        ProductInOrder productInOrder = new ProductInOrder(product, order, productAmount, product.getPrice());

        when(productService.findProductById(1L)).thenReturn(Optional.of(Optional.ofNullable(product).get()));
        when(orderService.findOrderById(1L)).thenReturn(Optional.of(Optional.ofNullable(order).get()));
        when(productInOrderRepository.save(new ProductInOrder(product, order, productAmount, product.getPrice()))).thenReturn(productInOrder);

        productInOrderService.addToOrder(product.getId(), order.getId(), productAmount);
        assertNotNull(order.getOrderPrice());
        assertNotNull(order.getAmount());
        assertEquals(orderPrice + productPrice * productAmount, order.getOrderPrice());
        assertEquals(orderAmount + productAmount, order.getAmount());
        verify(productService, times(1)).findProductById(1L);
        verify(orderService, times(1)).updateOrder(order);
        verify(orderService, times(1)).findOrderById(1L);
        verify(productInOrderRepository, times(1)).save(productInOrder);
    }
}