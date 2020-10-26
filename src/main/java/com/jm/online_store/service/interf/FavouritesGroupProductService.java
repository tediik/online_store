package com.jm.online_store.service.interf;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface FavouritesGroupProductService {
    void deleteProductFromFavouritesGroup(Product product, FavouritesGroup favouritesGroup);
    void addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup);
    Set<Product> getProductSet(FavouritesGroup favouritesGroup);
}
