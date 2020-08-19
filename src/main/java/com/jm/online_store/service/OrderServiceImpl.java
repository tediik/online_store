package com.jm.online_store.service;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByUserId(Long id) {
        return orderRepository.findByUserId(id);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAllByStatusEquals(String status) {
        return orderRepository.findAllByStatusEquals(status);
    }

    /**
     * Method calculates amount and order price before saving object
     *
     * @param order the object to save to the database
     */
    @Override
    public void addOrder(Order order) {
        Set<Product> products = order.getProducts();
        Long amount = Long.valueOf(0);
        Double orderPrice = Double.valueOf(0);
        for (Product product : products) {
            amount++;
            orderPrice += product.getPrice();
        }
        order.setAmount(amount);
        order.setOrderPrice(orderPrice);
        orderRepository.save(order);
    }
}
