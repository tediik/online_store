package com.jm.online_store.repository;

import com.jm.online_store.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
   // Role findByName(String name);
}
