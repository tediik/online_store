package com.jm.online_store.service.impl;

import com.jm.online_store.model.Basket;
import com.jm.online_store.repository.BasketRepository;
import com.jm.online_store.service.interf.BasketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * слой сервиса - бизнеслогики для сущности Basket.
 */
@Service
@AllArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;

    /**
     * метод поиска корзины по id.
     *
     * @param idBasket
     * @return корзина.
     */
    @Override
    public Basket findBasketById(Long idBasket) {
        return basketRepository.findById(idBasket).get();
    }

    /**
     * метод обновления корзины.
     *
     * @param basket корзина для обновления.
     * @return корзина.
     */
    @Override
    public Basket updateBasket(Basket basket) {
        return basketRepository.saveAndFlush(basket);
    }
}
