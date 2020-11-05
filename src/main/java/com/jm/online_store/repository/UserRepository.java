package com.jm.online_store.repository;

import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    User findByFirstName(String username);

    boolean existsByEmail(String email);

}
