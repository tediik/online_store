package com.jm.online_store.service.interf;

import com.jm.online_store.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<Product> findProductById(Long productId);

    Optional<Product> findProductByName(String productName);

    Long saveProduct(Product product);

    void deleteProduct(Long idProduct);

    List<Product> findAllByIdBefore(Long id);
}
