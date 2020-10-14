package com.jm.online_store.repository;

import com.jm.online_store.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    Optional<Categories> findByCategory(String category);

    boolean existsCategoriesById(long id);

    boolean existsCategoriesByCategory(String category);
}
