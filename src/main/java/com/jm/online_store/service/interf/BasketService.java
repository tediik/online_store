package com.jm.online_store.service.interf;

import com.jm.online_store.model.SubBasket;

import java.util.List;

public interface BasketService {
    SubBasket addBasket(SubBasket subBasket);
    SubBasket findBasketById(Long idBasket);
    SubBasket updateBasket(SubBasket subBasket, int difference);
    List<SubBasket> getBasket(String sessionID);
    void buildOrderFromBasket(Long id);
    void deleteBasket(SubBasket subBasket,String sessionID);

    void addProductToBasket(Long id,String sessionID);
}
