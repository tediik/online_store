package com.jm.online_store.service.interf;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;

import java.util.Set;

public interface FavouriteGoodsService {
    void deleteFromFavouriteGoods(Long id, User currentUser);
    void addToFavouriteGoods(Long id, User currentUser);
    Set<Product> getFavouriteGoods(User currentUser);
}