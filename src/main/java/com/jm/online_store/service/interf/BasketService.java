package com.jm.online_store.service.interf;

import com.jm.online_store.model.Basket;

/**
 * интерфейс для бизнеслогики корзины.
 */
public interface BasketService {
    Basket addBasket(Basket basket);
    Basket findBasketById(Long idBasket);
    Basket updateBasket(Basket basket);
}
