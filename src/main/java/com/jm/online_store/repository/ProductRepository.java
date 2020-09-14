package com.jm.online_store.repository;

import com.jm.online_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProduct(String productName);

    @Query(value = "SELECT * FROM Product LIMIT :num", nativeQuery = true)
    List<Product> findNumProducts(@Param("num") Integer num);
}
