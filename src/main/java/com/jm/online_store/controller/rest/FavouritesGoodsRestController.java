package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class FavouritesGoodsRestController {
    @GetMapping(value = "/customer/favouritesGoods")
    public ResponseEntity<Set<Product>> getUser(Authentication authentication) {
        User autorityUser = (User)authentication.getPrincipal();
        Set<Product> favouritesGoods = autorityUser.getFavouritesGoods();
        return (autorityUser == null) ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(favouritesGoods, HttpStatus.OK);
    }
}
