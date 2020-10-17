package com.jm.online_store.service.interf;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    Optional<Product> findProductById(Long productId);

    Optional<Product> findProductByName(String productName);

    Long saveProduct(Product product);

    void deleteProduct(Long idProduct);

    void restoreProduct(Long idProduct);

    List<Product> findAll();

    List<Product> getNotDeleteProducts();

    void importFromXMLFile(String fileName);

    void importFromCSVFile(String fileName) throws FileNotFoundException;

    List<Product> findNumProducts(Integer num);

    Map getProductPriceChange(Long idProduct);

    double changeProductRating(Long productId, double rating, User user);

    Optional<ProductDto> getProductDto(Long productI, User user);

    List<Product> findProductsByNameContains(String searchString);

    List<Product> findProductsByDescriptionContains(String searchString);

    boolean addNewSubscriber(Long id, String email);

    Long editProduct(Product product);

    Categories findProductCategory(Long id);
}
