package com.jm.online_store.service.impl;

import com.jm.online_store.model.Role;
import com.jm.online_store.repository.RoleRepository;
import com.jm.online_store.service.interf.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public List<Role> findAll() {
       return roleRepository.findAll();
    }

    @Override
    public void addRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
