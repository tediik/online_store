package com.jm.online_store.service.impl;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.FavouritesGroupRepository;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@AllArgsConstructor
public class FavouritesGroupProductServiceImpl implements FavouritesGroupProductService {
    private final FavouritesGroupRepository favouritesGroupRepository;

    /**
     * Удаление продукта из списка избранных товаров
     * @param product  продукт
     * @param favouritesGroup список
     */
    @Override
    public void deleteProductFromFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.remove(product);
        favouritesGroupRepository.save(favouritesGroup);
    }

    /**
     * Добавление продукта в список избранных товаров
     * @param product продукт
     * @param favouritesGroup список
     */
    @Override
    public void addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.add(product);
        favouritesGroupRepository.save(favouritesGroup);
    }

    /**
     * Получаем список продуктов из списка избранного
     * @param favouritesGroup список как сущность избранных товаров
     * @return список товаров
     */
    @Override
    public Set<Product> getProductSet(FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        return productSet;
    }
}
