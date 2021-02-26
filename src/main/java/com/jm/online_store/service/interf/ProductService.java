package com.jm.online_store.service.interf;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

	Optional<Product> findProductById(Long productId);

	Product getProductById(Long productId);

    Optional<Product> findProductByName(String productName);

    List<Product> findAllOrderByRatingAsc();

    List<Product> findAllOrderByRatingDesc();

    Long saveProduct(Product product);

    void deleteProduct(Long idProduct);

    int findProductAmount(Long idProduct);

    void restoreProduct(Long idProduct);

    List<Product> findAll();

    List<Product> getNotDeleteProducts();

    List<Product> findProductsByCategoryName(String categoryName);

    void importFromXMLFile(String fileName);

    void importFromXMLFile(MultipartFile multipartFile);

    void importFromXMLFile(String fileName, Long categoryId);

    void importFromCSVFile(String fileName) throws FileNotFoundException;

    void importFromCSVFile(MultipartFile multipartFile) throws FileNotFoundException;

    void importFromCSVFile(String fileName, Long categoryId) throws FileNotFoundException;

    List<Product> findNumProducts(Integer num);

    Map getProductPriceChange(Long idProduct);

    double changeProductRating(Long productId, double rating, User user);

    Optional<ProductDto> getProductDto(Long productI, User user);

    List<Product> findProductsByNameContains(String searchString);

    List<Product> findProductsByDescriptionContains(String searchString);

    boolean addNewSubscriber(ObjectNode body);

    Long editProduct(Product product);

    void saveAllProducts(List<Product> products);

    XSSFWorkbook createXlsxDoc(List<Product> products, String category);

    boolean existsProductByProduct(String productName);

    List<Product> findTrackableProductsByLoggedInUser();

    void deleteProductFromTrackedForLoggedInUser(long productId);

    String findProductPictureNamesById(Long id);

    void deleteProductPictureNameById(Long id);

    Long countAllPictureProductById(Long id);

    Long findProductIdByIdPicture(Long id);
}
