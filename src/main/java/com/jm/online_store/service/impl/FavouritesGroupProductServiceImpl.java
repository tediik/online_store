package com.jm.online_store.service.impl;

import com.jm.online_store.exception.FavouritesGroupNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavouritesGroupProductServiceImpl implements FavouritesGroupProductService {
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;

    @Override
    public void deleteProductFromFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {

    }

    @Override
    public void addProductToFavouritesGroup(Product product, FavouritesGroup favouritesGroup) {

    }
}
