package com.jm.online_store.repository;

import com.jm.online_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProduct(String productName);

    List<Product> findAllByIdBefore(Long id);
}
