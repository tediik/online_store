package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

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

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
