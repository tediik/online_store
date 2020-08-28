package com.jm.online_store.service.impl;

import com.jm.online_store.model.SubBasket;
import com.jm.online_store.repository.BasketRepository;
import com.jm.online_store.service.interf.BasketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;

    @Override
    public SubBasket findBasketById(Long idBasket) {
        return basketRepository.findById(idBasket).get();
    }

    @Override
    public SubBasket updateBasket(SubBasket subBasket) {
        return basketRepository.saveAndFlush(subBasket);
    }

    @Override
    public SubBasket addBasket(SubBasket subBasket) {
        return basketRepository.save(subBasket);
    }

    @Override
    public void deleteBasket(SubBasket subBasket) {
        basketRepository.delete(subBasket);
    }
}
