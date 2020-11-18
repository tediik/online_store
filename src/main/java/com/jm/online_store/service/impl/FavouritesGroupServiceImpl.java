package com.jm.online_store.service.impl;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.FavouritesGroupRepository;
import com.jm.online_store.service.interf.FavouritesGroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class FavouritesGroupServiceImpl implements FavouritesGroupService {

    private final FavouritesGroupRepository favouritesGroupRepository;

    @Override
    public List<FavouritesGroup> findAll() {
        return favouritesGroupRepository.findAll();
    }

    @Override
    public List<FavouritesGroup> findAllByUser(User user) {
        return favouritesGroupRepository.findAllByUser(user);
    }

    /**
     * Add specific product , to specific favorite group
     * @param product product
     * @param favouritesGroup favorite group
     */
    @Override
    public void addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.add(product);
        favouritesGroupRepository.save(favouritesGroup);
    }

    @Override
    public void addFavouritesGroup(FavouritesGroup favouritesGroup) {
        favouritesGroupRepository.save(favouritesGroup);
    }

    /**
     * Completely delete all group
     * @param id group Id
     */
    @Override
    public void deleteById(Long id) {
        favouritesGroupRepository.deleteById(id);
    }

    /**
     * Remove specific product from specific group
     * @param product  product
     * @param favouritesGroup group
     */
    @Override
    public void deleteSpecificProductFromSpecificFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.remove(product);
        favouritesGroupRepository.save(favouritesGroup);
    }

    @Override
    public Optional<FavouritesGroup> findById(Long id) {
        return favouritesGroupRepository.findById(id);
    }

    @Override
    public Optional<FavouritesGroup> findByName(String name) {
        return favouritesGroupRepository.findByName(name);
    }

    @Override
    public FavouritesGroup getOneFavouritesGroupByUserAndByName(User user, String name) {
        return favouritesGroupRepository.getOneFavouritesGroupByUserAndByName(user, name);
    }

    @Override
    public void save(FavouritesGroup favouritesGroup) {
        favouritesGroupRepository.save(favouritesGroup);
    }
}
