package com.jm.online_store.service;

import com.jm.online_store.model.Order;
import com.jm.online_store.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findAllByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    public List<Order> findAllByUserIdAndStatus(Long userId, Order.Status status) {
        return orderRepository.findAllByUserIdAndStatus(userId, status);
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Long addOrder(Order order) {
        order.setAmount(Long.valueOf(0));
        order.setOrderPrice(Double.valueOf(0));
        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }
}
