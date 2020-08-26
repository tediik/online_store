package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/admin")
public class AdminRestController {

    private final UserService userService;

    @GetMapping(value = "/authUser")
    public ResponseEntity<User> showAuthUserInfo(Authentication authentication){
        User principal = (User) authentication.getPrincipal();
        User authUser = userService.getOne(principal.getId());
        return new ResponseEntity<>(authUser, HttpStatus.OK);
    }
}
