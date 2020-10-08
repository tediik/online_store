package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class FavouritesGroupRestController {
    private final FavouritesGroupService favouritesGroupService;

    @GetMapping(value = "/customer/favouritesGroup")
    public ResponseEntity<List<FavouritesGroup>> getFavouritesGroups(@AuthenticationPrincipal User user) {
        System.out.println(favouritesGroupService.findAll().toString());
        return ResponseEntity.ok(favouritesGroupService.findAll());
    }
}
