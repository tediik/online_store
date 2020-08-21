package com.jm.online_store.service.impl;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductInOrder;
import com.jm.online_store.repository.ProductInOrderRepository;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductInOrderServiceImpl implements ProductInOrderService {

    private final ProductService productService;
    private final OrderService orderService;
    private final ProductInOrderRepository repository;

    public void addToOrder(long productId, long orderId, int amount) {
        Product product = productService.findProductById(productId).get();
        Order order = orderService.findOrderById(orderId).get();

        order.setOrderPrice(order.getOrderPrice() + product.getPrice());
        order.setAmount(order.getAmount() + amount);

        orderService.updateOrder(order);

        ProductInOrder productInOrder = new ProductInOrder(product, order, amount);
        repository.save(productInOrder);
    }
}
