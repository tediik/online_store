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

    /**
     * Метод добавления продукта в заказ с учётом количества.
     * Находит product и order по переданным id в соответствующих таблицах,
     * обновляет поля order, хранящие сумму заказа и общеее кол-во продуктов в нём,
     * согласно переданному кол-ву и цене продукта.
     * Затем сохранет order, формирует новый экземпляр ProductInOrder и сохраняет его.
     *
     * @param productId id заказываемого продукта в таблице "product"
     * @param orderId   id заказа, в который добавляется продукт, в таблице "orders"
     * @param amount    количество продукта в заказе
     */
    public void addToOrder(long productId, long orderId, int amount) {
        Product product = productService.findProductById(productId).get();
        Order order = orderService.findOrderById(orderId).get();

        order.setOrderPrice(order.getOrderPrice() + product.getPrice() * amount);
        order.setAmount(order.getAmount() + amount);

        orderService.updateOrder(order);

        ProductInOrder productInOrder = new ProductInOrder(product, order, amount, product.getPrice());
        repository.save(productInOrder);
    }
}
