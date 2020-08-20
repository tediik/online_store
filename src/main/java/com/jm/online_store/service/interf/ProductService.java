package com.jm.online_store.service.interf;

import com.jm.online_store.model.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> findProductById(Long productId);

    void saveProduct (Product product);

    void deleteProduct(Long idProduct);
}
