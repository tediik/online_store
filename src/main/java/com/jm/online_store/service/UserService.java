package com.jm.online_store.service;

import com.jm.online_store.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User>findById(Long id);

    void addUser(User user);

    void deleteByID(Long id);

    void updateUser(User user);

    Optional<User> findByEmail(String username);

    boolean isUserExistsByEmail(String email);







}
