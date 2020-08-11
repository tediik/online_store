package com.jm.online_store.service;

import com.jm.online_store.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Role>findAll();

    void addRole(Role role);

    Optional<Role>findByName(String name);
}
