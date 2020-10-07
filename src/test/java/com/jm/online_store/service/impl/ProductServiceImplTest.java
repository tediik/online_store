package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
class ProductServiceImplTest {
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final ProductService productService = new ProductServiceImpl(productRepository);
    private Product product;

    @BeforeEach
    void init() {
        product = new Product();
        product.setId(1L);
        product.setProduct("Ledger");
        log.info("startup");
    }

    @Test
    void findProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        Optional<Product> optionalProduct = productService.findProductById(1L);
        assertNotNull(optionalProduct);
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void findProductByName() {
        when(productRepository.findByProduct(product.getProduct())).thenReturn(Optional.ofNullable(product));
        Optional<Product> optionalProduct = productService.findProductByName(product.getProduct());
        assertNotNull(optionalProduct);
        verify(productRepository, times(1)).findByProduct(product.getProduct());
    }

    @Test
    void saveProduct() {
        when(productRepository.save(product)).thenReturn(product);
        Long id = productService.saveProduct(product);
        assertNotNull(id);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct() {
        when(productRepository.getOne(1L)).thenReturn(product);
        productService.deleteProduct(product.getId());
        assertEquals(true, product.getDeleteStatus());
        verify(productRepository, times(1)).save(product);
    }

    @AfterEach
    void after() {
        product = null;
        log.info("finalize");
    }
}