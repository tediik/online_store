package com.jm.online_store.repository;

import com.jm.online_store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProduct(String productName);

    @Query(value = "SELECT * FROM Product LIMIT :num", nativeQuery = true)
    List<Product> findNumProducts(@Param("num") Integer num);

    List<Product> findProductByProductContains(String searchString);

    @Query("FROM Product p WHERE p.descriptions.information LIKE %:searchString%")
    List<Product> findProductByDescriptionsContains(@Param("searchString") String searchString);

    @Query(value = " FROM Product  WHERE category_id = :id")
    List<Product> getProductsByCategoryId(@Param("id")Long id);
}