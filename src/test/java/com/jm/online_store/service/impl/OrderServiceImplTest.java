package com.jm.online_store.service.impl;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.OrderRepository;
import com.jm.online_store.service.interf.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderService orderService = new OrderServiceImpl(orderRepository);
    private List<Order> orderList;
    Order order1;
    Order order2;
    User user;

    @BeforeEach
    void init() {
        orderList = new ArrayList<>();
        user = new User();
        user.setId(1L);
        order1 = new Order(LocalDateTime.now(), Order.Status.COMPLETED);
        order1.setId(1L);
        order1.setUser(user);
        order1.setAmount(0L);
        order1.setOrderPrice((double) 0);
        order2 = new Order(LocalDateTime.now(), Order.Status.COMPLETED);
        order2.setId(2L);
        order2.setUser(user);
        order2.setAmount(0L);
        order2.setOrderPrice((double) 0);
        orderList.add(order1);
        orderList.add(order2);
    }

    @Test
    void findAll() {
        when(orderRepository.findAll()).thenReturn(orderList);
        List<Order> testOrderList = orderService.findAll();
        assertEquals(2, testOrderList.size());
        assertFalse(testOrderList.isEmpty());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void findAllByUserId() {
        when(orderRepository.findAllByUserId(1L))
                .thenReturn(orderList.stream()
                    .filter(order -> order.getUser().getId().equals(1L))
                    .collect(Collectors.toList()));

        List<Order> userOrdersList = orderRepository.findAllByUserId(1L);
        assertEquals(2, userOrdersList.size());
        assertFalse(userOrdersList.isEmpty());
        verify(orderRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    void findAllByUserIdAndStatus() {
        when(orderRepository.findAllByUserIdAndStatus(1L, Order.Status.COMPLETED))
                .thenReturn(orderList.stream()
                    .filter(order -> order.getUser().getId().equals(1L))
                    .filter(order -> order.getStatus().equals(Order.Status.COMPLETED))
                    .collect(Collectors.toList()));

        List<Order> orderListByUserIdAndStatus = orderService
                .findAllByUserIdAndStatus(1L, Order.Status.COMPLETED);

        assertEquals(2, orderListByUserIdAndStatus.size());
        assertFalse(orderListByUserIdAndStatus.isEmpty());
        verify(orderRepository, times(1))
                .findAllByUserIdAndStatus(1L, Order.Status.COMPLETED);
    }

    @Test
    void findOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(order1));
        Optional<Order> optionalOrder = orderService.findOrderById(1L);
        assertNotNull(optionalOrder.get());
        assertEquals(1L, optionalOrder.get().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void addOrder() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.save(order)).thenReturn(order);
        Long id = orderService.addOrder(order);
        assertNotNull(id);
        assertNotNull(order.getAmount());
        assertNotNull(order.getOrderPrice());
        verify(orderRepository, times(1)).save(order);
        order = null;
    }

    @Test
    void updateOrder() {
        orderService.updateOrder(order1);
        verify(orderRepository, times(1)).save(order1);
    }

    @AfterEach
    void after() {
        orderList.clear();
        order1 = null;
        order2 = null;
        user = null;
    }
}