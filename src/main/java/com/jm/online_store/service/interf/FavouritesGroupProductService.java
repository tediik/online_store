package com.jm.online_store.service.interf;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;

import java.util.Set;

public interface FavouritesGroupProductService {

    void addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup);

    Set<Product> getProductSet(FavouritesGroup favouritesGroup);

}
