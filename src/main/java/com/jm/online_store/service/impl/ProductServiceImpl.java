package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * метод поиска Product по иденификатору.
     *
     * @param productId идентификатор Product
     * @return Optional<Product>
     */
    @Override
    public Optional<Product> findProductById(Long productId) {
        return productRepository.findById(productId);
    }

    /**
     * метод поиска Product по наименованию.
     *
     * @param productName наименование Product
     * @return Optional<Product>
     */
    @Override
    public Optional<Product> findProductByName(String productName) {
        return productRepository.findByProduct(productName);
    }

    /**
     * метод обновления Product.
     *
     * @param product экземпляр класса Product
     * @return идентификатор обновленного Product
     */
    @Override
    public Long saveProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    /**
     * метод удаления Product.
     *
     * @param idProduct идентификатор Product
     */
    @Override
    public void deleteProduct(Long idProduct) {
        productRepository.deleteById(idProduct);
    }

    /**
     * метод выбора всех Product, идентификатор которых меньше переданного.
     *
     * @param id идентификатор Product
     * @return List<Product>
     */
    @Override
    public List<Product> findAllByIdBefore(Long id) {
        return productRepository.findAllByIdBefore(id);
    }
}
