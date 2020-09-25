package com.jm.online_store.repository;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.ProductDto;
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

//    @Query(value = "select new com.jm.online_store.model.dto.ProductDto(p.id, p.comments) FROM Product p WHERE p.id = :id")
//    ProductDto getCommentProductDto(@Param("id") Long productId);


}
