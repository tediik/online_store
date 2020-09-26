package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/filter/{role}")
@RequiredArgsConstructor
public class UserFilterController {

    private final UserService userService;

    @PutMapping
    public List<User> filterByRoles(@PathVariable String role){
        return userService.findByRole(role);
    }
}
