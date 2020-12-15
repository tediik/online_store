package com.jm.online_store.repository;

import com.jm.online_store.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    Optional<Categories> findByCategory(String category);

    List<Categories> getCategoriesByParentCategoryId(Long parentCategoryId);

    @Query("FROM Categories c WHERE c.parentCategoryId > 0")
    List<Categories> getCategoriesWithoutParentCategory();

    List<Categories> findAll();

}
