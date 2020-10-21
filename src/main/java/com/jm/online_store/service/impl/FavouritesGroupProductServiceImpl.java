package com.jm.online_store.service.impl;

import com.jm.online_store.exception.FavouritesGroupNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.FavouritesGroupRepository;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavouritesGroupProductServiceImpl implements FavouritesGroupProductService {
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;
    private final FavouritesGroupRepository favouritesGroupRepository;
    private final UserService userService;

    @Override
    public void deleteProductFromFavouritesGroup(Product product, FavouritesGroup favouritesGroup, User user) {
        Set<FavouritesGroup> favouritesGroupSet = user.getFavouritesGroups();
        FavouritesGroup selectedFavouritesGroup = favouritesGroupSet.stream().filter(data -> Objects.equals(data, favouritesGroup)).findFirst().get();
        Set<Product> productSet = selectedFavouritesGroup.getProducts();
        productSet.remove(product);
        selectedFavouritesGroup.setProducts(productSet);
        user.setFavouritesGroups(favouritesGroupSet);
        userService.updateUser(user);
    }
    public void deleteListProductFromFavouritesGroup(ArrayList<Long> idProducts, FavouritesGroup favouritesGroup, User user) {
//        Set<FavouritesGroup> favouritesGroupSet = user.getFavouritesGroups();
//        FavouritesGroup selectedFavouritesGroup = favouritesGroupSet.stream().filter(data -> Objects.equals(data, favouritesGroup)).findFirst().get();
//        Set<Product> productSet = selectedFavouritesGroup.getProducts();
//        productSet.remove(product);
//        selectedFavouritesGroup.setProducts(productSet);
//        user.setFavouritesGroups(favouritesGroupSet);
//        userService.updateUser(user);
    }

    @Override
    public void addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup, User user) {
        Set<FavouritesGroup> favouritesGroupSet = user.getFavouritesGroups();
        FavouritesGroup selectedFavouritesGroup = favouritesGroupSet.stream().filter(data -> Objects.equals(data, favouritesGroup)).findFirst().get();
        Set<Product> productSet = selectedFavouritesGroup.getProducts();
        productSet.add(product);
        selectedFavouritesGroup.setProducts(productSet);
        user.setFavouritesGroups(favouritesGroupSet);
        userService.updateUser(user);
    }

    @Override
    public Set<Product> getProductSet(FavouritesGroup favouritesGroup, User user) {
        Set<FavouritesGroup> favouritesGroupSet = user.getFavouritesGroups();
        FavouritesGroup selectedFavouritesGroup = favouritesGroupSet.stream().filter(data -> Objects.equals(data, favouritesGroup)).findFirst().get();
        Set<Product> productSet = selectedFavouritesGroup.getProducts();
        return productSet;
    }
}
