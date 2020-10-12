package com.jm.online_store.service.interf;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;

import java.util.Set;

public interface FavouritesGroupProductService {
    void deleteProductFromFavouritesGroup(Long idProduct, Long idFavouritesGroup, User currentUser);

    void addProductToFavouritesGroup(Long idProduct, Long idFavouritesGroup, User currentUser);

    Set<Product> getProductFromFavouritesGroup(Long idFavouritesGroup, User currentUser);
}
