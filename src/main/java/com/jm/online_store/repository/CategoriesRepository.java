package com.jm.online_store.repository;

import com.jm.online_store.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CategoriesRepository extends JpaRepository< Categories, Long >, CrudRepository< Categories, Long> {
}
