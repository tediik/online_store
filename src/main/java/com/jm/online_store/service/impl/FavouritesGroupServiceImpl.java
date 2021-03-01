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

    /**
     * find all existing favorite groups
     * @return list
     */
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
    public FavouritesGroup addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.add(product);
        return favouritesGroupRepository.save(favouritesGroup);
    }

    /**
     * Add new favorites Group
     * @param favouritesGroup favorite group
     */
    @Override
    public void addFavouritesGroup(FavouritesGroup favouritesGroup) {
        favouritesGroupRepository.save(favouritesGroup);
    }

    /**
     * Completely delete all group from DB
     * @param id group Id
     */
    @Override
    public void deleteById(Long id) {
        favouritesGroupRepository.deleteById(id);
    }

    /**
     * Remove specific product from specific group and save changes to DB
     * @param product  product
     * @param favouritesGroup group
     */
    @Override
    public void deleteSpecificProductFromSpecificFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.remove(product);
        favouritesGroupRepository.save(favouritesGroup);
    }

    /**
     * Find favorite group by Id
     * @param id favorite group Id
     * @return favorite group
     */
    @Override
    public Optional<FavouritesGroup> findById(Long id) {
        return favouritesGroupRepository.findById(id);
    }

    /**
     * Find favorite group by group name
     * @param name group name
     * @return FavouritesGroup
     */
    @Override
    public Optional<FavouritesGroup> findByName(String name) {
        return favouritesGroupRepository.findByName(name);
    }

    /**
     * Find and retrieve favoriteGroup from DB, by name of group and user of this group
     * @param user user of that group
     * @param name name of Favorite Group
     * @return FavouritesGroup
     */
    @Override
    public FavouritesGroup getOneFavouritesGroupByUserAndByName(User user, String name) {
        return favouritesGroupRepository.getOneFavouritesGroupByUserAndByName(user, name);
    }

    /**
     * Getting Set from FavouritesGroup
     * @param favouritesGroup entity
     * @return set from entity
     */
    @Override
    public Set<Product> getProductSet(FavouritesGroup favouritesGroup) {
        Set<Product> productSet = favouritesGroup.getProducts();
        return productSet;
    }

    /**
     * Save new group in DB
     * @param favouritesGroup FavouritesGroup
     */
    @Override
    public void save(FavouritesGroup favouritesGroup) {
        favouritesGroupRepository.save(favouritesGroup);
    }

}
