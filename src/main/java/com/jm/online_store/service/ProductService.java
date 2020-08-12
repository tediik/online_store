package com.jm.online_store.service;

import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Optional<Product> findProductById(Long productId){
        return productRepository.findById(productId);
    }

    public void saveProduct (Product product){
        productRepository.save(product);
    }

    public void deleteProduct(Long idProduct){
        productRepository.deleteById(idProduct);
    }
}
