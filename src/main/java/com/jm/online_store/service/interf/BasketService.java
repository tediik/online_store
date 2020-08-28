package com.jm.online_store.service.interf;

import com.jm.online_store.model.SubBasket;

public interface BasketService {
    SubBasket addBasket(SubBasket subBasket);
    SubBasket findBasketById(Long idBasket);
    SubBasket updateBasket(SubBasket subBasket);
    void deleteBasket(SubBasket subBasket);
}
