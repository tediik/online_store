package com.jm.online_store.repository;

import com.jm.online_store.model.FavouritesGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavouritesGroupRepository extends JpaRepository<FavouritesGroup, Long> {
    void deleteById(Long id);
}
