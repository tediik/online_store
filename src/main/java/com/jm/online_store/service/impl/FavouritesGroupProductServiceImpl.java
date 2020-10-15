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
    private final UserService userService;
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;

    @Override
    public void deleteProductFromFavouritesGroup(Long idProduct, Long idFavouritesGroup, User currentUser) {
        Iterator<FavouritesGroup> favouritesGroupIterator = currentUser.getFavouritesGroups().iterator();
        FavouritesGroup favouritesGroup = null;
        while (favouritesGroupIterator.hasNext()) {
            favouritesGroup = favouritesGroupIterator.next();
            if (favouritesGroup.getId() == idFavouritesGroup){
                break;
            }
        }
        Iterator<Product> productIterator = favouritesGroup.getProducts().iterator();
        Product product = null;
        while (productIterator.hasNext()){
            product = productIterator.next();
            if (product.getId() == idProduct) {
                favouritesGroup.getProducts().remove(product);
                break;
            }
        }
    }

    @Override
    public void addProductToFavouritesGroup(Long idProduct, Long idFavouritesGroup, User currentUser) {
        User user = userService.findById(currentUser.getId()).orElseThrow(UserNotFoundException::new);
        Iterator iterator = user.getFavouritesGroups().iterator();
        FavouritesGroup favouritesGroup = null;
        while (iterator.hasNext()) {
            favouritesGroup = (FavouritesGroup) iterator.next();
            if (favouritesGroup.getId() == idFavouritesGroup) {
                break;
            }
        }
        Set<Product> productSet = favouritesGroup.getProducts();
        productSet.add(productService.findProductById(idProduct).orElseThrow(ProductNotFoundException::new));
        favouritesGroup.setProducts(productSet);
    }

    @Override
    public Set<Product> getProductFromFavouritesGroup(Long idFavouritesGroup, User currentUser) {
        return null;
    }
}
