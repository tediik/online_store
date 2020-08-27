package com.jm.online_store.service.impl;

import com.jm.online_store.model.SubBasket;
import com.jm.online_store.repository.BasketRepository;
import com.jm.online_store.service.interf.BasketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * слой сервиса - бизнеслогики для сущности SubBasket.
 */
@Service
@AllArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;

    /**
     * метод поиска корзины по id.
     *
     * @param idBasket идентификатор корзины
     * @return корзина.
     */
    @Override
    public SubBasket findBasketById(Long idBasket) {
        return basketRepository.findById(idBasket).get();
    }

    /**
     * метод обновления корзины.
     *
     * @param subBasket корзина для обновления.
     * @return корзина.
     */
    @Override
    public SubBasket updateBasket(SubBasket subBasket) {
        return basketRepository.saveAndFlush(subBasket);
    }

    /**
     * метод добавления корзины.
     *
     * @param subBasket корзина для обновления.
     * @return корзина.
     */
    @Override
    public SubBasket addBasket(SubBasket subBasket) {
        return basketRepository.save(subBasket);
    }

    /**
     * метод удаления корзины.
     *
     * @param subBasket корзина.
     */
    @Override
    public void deleteBasket(SubBasket subBasket) {
        basketRepository.delete(subBasket);
    }
}
