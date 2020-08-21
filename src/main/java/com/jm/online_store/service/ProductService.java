package com.jm.online_store.service;

import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Optional<Product> findProductById(Long productId) {
        return productRepository.findById(productId);
    }

    public Long saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    public void deleteProduct(Long idProduct) {
        productRepository.deleteById(idProduct);
    }
}
