package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import com.jm.online_store.service.interf.FavouritesGroupService;
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

    @PostMapping(value = "/customer/addProductInFavouritesGroup/{id}")
    public ResponseEntity addProductInFavouritesGroup(@RequestBody Product product, @PathVariable("id") Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroupProductService.addProductToFavouritesGroup(product, favouritesGroup, user);
        return ResponseEntity.ok("addProductInFavouritesGroupOK");
    }

    @GetMapping(value = "/customer/getProductFromFavouritesGroup/{id}")
    public ResponseEntity<Set<Product>> getProductFromFavouritesGroup (@PathVariable Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        //favouritesGroupProductService.getProductSet(favouritesGroup, user);
        return ResponseEntity.ok(favouritesGroupProductService.getProductSet(favouritesGroup, user));
    }

    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroup/{id}")
    public ResponseEntity deleteProductFromFavouritesGroup(@RequestBody Product product, @PathVariable("id") Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroupProductService.deleteProductFromFavouritesGroup(product, favouritesGroup, user);
        return ResponseEntity.ok("deleteProductFromFavouritesGroupOK");
    }

    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroupAll")
    public ResponseEntity deleteFromFavouritesGroupProductAll(@RequestBody Long id) {
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/customer/deleteProductFromFavouritesGroup/{idNewGroup}/{idOldGroup}")
    public ResponseEntity moveProducts(@RequestBody ArrayList<Long> idProducts, @PathVariable("idNewGroup") Long idNewGroup, @PathVariable("idOldGroup") Long idOldGroup) {
        System.out.println("idNewGroup=" + idNewGroup + "      idOldGroup=" + idOldGroup + "       idProducts=" + idProducts);
        return ResponseEntity.ok().build();
    }
}

