package com.jm.online_store.service;

import com.jm.online_store.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> findAll();

    List<Order> findByUserId(Long id);

    Optional<Order> findById(Long id);

    List<Order> findAllByStatusEquals(String status);

    void addOrder(Order order);
}
