package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/users")
public class UserRestController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        Set<Role> resultRoleSet = user.getRoles()
                .stream()
                .map(role -> roleService.findByName(role.getAuthority()).get())
                .collect(Collectors.toSet());
        user.setRoles(resultRoleSet);
        userService.addUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user){
        Set<Role> resultRoleSet = user.getRoles()
                .stream()
                .map(role -> roleService.findByName(role.getAuthority()).get())
                .collect(Collectors.toSet());
        user.setRoles(resultRoleSet);
        userService.updateUser(user);
        return user;
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId){
        userService.deleteByID(userId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
