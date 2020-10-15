package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
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

    @PostMapping(value = "/customer/addProductInFavouritesGroup")
    public ResponseEntity addProductInFavouritesGroup(@RequestBody ArrayList<Long> idPidG) {
        User user = userService.getCurrentLoggedInUser();
        Long idFavouritesGroup = idPidG.get(idPidG.size() - 1);
        Long idProduct;
        for (int i = 0; i < idPidG.size() - 1; i++) {
            idProduct = idPidG.get(i);
            favouritesGroupProductService.addProductToFavouritesGroup(idProduct, idFavouritesGroup, user);
        }
        return ResponseEntity.ok("addProductInFavouritesGroupOK");
    }

    @GetMapping(value = "/customer/getProductFromFavouritesGroup/{id}")
    public ResponseEntity<Set<Product>> getProductFromFavouritesGroup (@PathVariable Long id) {
        User user = userService.getCurrentLoggedInUser();
        Set<FavouritesGroup> favouritesGroupSet = user.getFavouritesGroups();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        return ResponseEntity.ok(favouritesGroup.getProducts());
    }

    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroup")
    public ResponseEntity deleteProductFromFavouritesGroup(@RequestBody ArrayList<Long> idPidG) { //  1 2   5
        User user = userService.getCurrentLoggedInUser();
        Set<FavouritesGroup> favouritesGroupSet = user.getFavouritesGroups();
        Long idFavouritesGroup = idPidG.get(idPidG.size() - 1);
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(idFavouritesGroup).orElseThrow();
        Long idProduct;
        for (int i = 0; i < idPidG.size() - 1; i++) {
            idProduct = idPidG.get(i);
            favouritesGroupProductService.deleteProductFromFavouritesGroup(idProduct, idFavouritesGroup, user);
        }
        return ResponseEntity.ok("deleteProductFromFavouritesGroupOK");
    }

}

