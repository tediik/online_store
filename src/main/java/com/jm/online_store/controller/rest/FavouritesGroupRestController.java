package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class FavouritesGroupRestController {
    private final FavouritesGroupService favouritesGroupService;
    private final UserService userService;

    @GetMapping(value = "/customer/favouritesGroup")
    public ResponseEntity getFavouritesGroups() {
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(favouritesGroupService.findAllByUser(user));
    }

    @PostMapping(value = "/customer/favouritesGroup")
    public ResponseEntity addFavouritesGroups(@RequestBody FavouritesGroup favouritesGroup) {
        User user = userService.getCurrentLoggedInUser();
        favouritesGroup.setUser(user);
        favouritesGroupService.addFavouritesGroup(favouritesGroup);
        return ResponseEntity.ok(favouritesGroupService.getOneFavouritesGroupByUserAndByName(user, favouritesGroup.getName()));
    }

    @DeleteMapping(value = "/customer/favouritesGroup/{id}")
    public void deleteFavouritesGroups(@PathVariable("id") Long id) {
        favouritesGroupService.deleteById(id);
    }
}
