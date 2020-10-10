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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


@RestController
@AllArgsConstructor
public class FavouritesGroupRestController {
    private final FavouritesGroupService favouritesGroupService;
    private final UserService userService;

    @GetMapping(value = "/customer/favouritesGroup")
    public ResponseEntity getFavouritesGroups() {
        System.out.println("Попали в GET");
        List<FavouritesGroup> favouritesGroupList = favouritesGroupService.findAll();
        System.out.println(favouritesGroupList);
        return ResponseEntity.ok(favouritesGroupList.toString());
    }
    @PostMapping(value = "/customer/favouritesGroup")
    public ResponseEntity addFavouritesGroups(@RequestBody FavouritesGroup favouritesGroup) {
        System.out.println("Попали в POST");
        //System.out.println(userService.getCurrentLoggedInUser());
        return ResponseEntity.ok("OK");
    }

}
