package com.jm.online_store.service.interf;

public interface ProductInOrderService {
    void addToOrder(long productId, long orderId, int amount);
}
