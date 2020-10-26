package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
public class ProductForFavouritesGroupRestController {
    private final UserService userService;
    private final FavouritesGroupProductService favouritesGroupProductService;
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;

    @PostMapping(value = "/customer/addProductInFavouritesGroup/{id}")
    public ResponseEntity addProductInFavouritesGroup(@RequestBody Product product, @PathVariable("id") Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroupProductService.addProductToFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok("addProductInFavouritesGroupOK");
    }

    @GetMapping(value = "/customer/getProductFromFavouritesGroup/{id}")
    public ResponseEntity<Set<Product>> getProductFromFavouritesGroup(@PathVariable Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        return ResponseEntity.ok(favouritesGroupProductService.getProductSet(favouritesGroup));
    }

    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroup/{idGroup}")
    public ResponseEntity deleteProductFromFavouritesGroup(@RequestBody Long idProduct, @PathVariable("idGroup") Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        Product product = productService.findProductById(idProduct).orElseThrow();
        favouritesGroupProductService.deleteProductFromFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/customer/clearFavouritesGroup/{idGroup}")
    public ResponseEntity deleteFromFavouritesGroupProductAll(@RequestBody ArrayList<Long> idProducts, @PathVariable("idGroup") Long idGroup) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(idGroup).orElseThrow();
        for (int i = 0; i < idProducts.size(); i++){
            Product product = productService.findProductById(idProducts.get(i)).orElseThrow();
            favouritesGroupProductService.deleteProductFromFavouritesGroup(product, favouritesGroup);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/customer/deleteProductFromFavouritesGroup/{idNewGroup}/{idOldGroup}")
    public ResponseEntity moveProducts(@RequestBody ArrayList<Long> idProducts, @PathVariable("idNewGroup") Long idNewGroup, @PathVariable("idOldGroup") Long idOldGroup) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup newFavouritesGroup = favouritesGroupService.findById(idNewGroup).orElseThrow();
        FavouritesGroup oldFavouritesGroup = favouritesGroupService.findById(idOldGroup).orElseThrow();
        for (int i = 0; i < idProducts.size(); i++) {
            Product product = productService.findProductById(idProducts.get(i)).orElseThrow();
            favouritesGroupProductService.deleteProductFromFavouritesGroup(product, oldFavouritesGroup);
            favouritesGroupProductService.addProductToFavouritesGroup(product, newFavouritesGroup);
        }
        return ResponseEntity.ok().build();
    }
}

