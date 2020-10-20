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

        return ResponseEntity.ok("addProductInFavouritesGroupOK");
    }

    @GetMapping(value = "/customer/getProductFromFavouritesGroup/{id}")
    public ResponseEntity<Set<Product>> getProductFromFavouritesGroup (@PathVariable Long id) {

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroup")
    public ResponseEntity deleteProductFromFavouritesGroup(@RequestBody ArrayList<Long> idPidG) {

        return ResponseEntity.ok("deleteProductFromFavouritesGroupOK");
    }
    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroupAll")
    public ResponseEntity deleteFromFavouritesGroupProductAll(@RequestBody Long id) {

        return ResponseEntity.ok().build();
    }
}

