package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
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

@RestController
@AllArgsConstructor
public class ProductForFavouritesGroupRestController {
    private final UserService userService;
    private final FavouritesGroupProductService favouritesGroupProductService;

    @PostMapping(value = "/customer/addProductInFavouritesGroup")
    public ResponseEntity addProductInFavouritesGroup(@RequestBody ArrayList<Long> idPidG) {
        User user = userService.getCurrentLoggedInUser();
        Long idFavouritesGroup = idPidG.get(idPidG.size() - 1);
        Long idProduct;
        for (int i = 0; i < idPidG.size() - 1; i++) {
            idProduct = idPidG.get(i);
            favouritesGroupProductService.addProductToFavouritesGroup(idProduct, idFavouritesGroup, user);
        }
        System.out.println("Мы в контроллере - ProductForFavouritesGroupRestController, в методе addProductInFavouritesGroup");
        return ResponseEntity.ok("Мы в addProductInFavouritesGroup метод POST");
    }
}

