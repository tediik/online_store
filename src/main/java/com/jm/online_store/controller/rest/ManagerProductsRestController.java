package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с добавлением/изменением товаров
 */
@RestController
@AllArgsConstructor
@Slf4j
public class ManagerProductsRestController {

    private final ProductService productService;
    private final CategoriesService categoriesService;

    /**
     * Метод обрабатывает загрузку файла с товарами на сервер
     * Вызывает соответствующий сервисный метод в зависимости от типа файла(CSV или XML)
     * @param file файл с данными
     * @return
     */
    @PostMapping(value = "/rest/products/uploadProductsFile")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws FileNotFoundException {
        try {
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File("uploads/import/" + file.getOriginalFilename())));
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            log.error("Ошибка сохранения файла");
            e.printStackTrace();
        }
        log.debug("тип файла" + getFileExtension(file.getOriginalFilename()));
        if (getFileExtension(getFileExtension(file.getOriginalFilename())).equals(".xml")) {
            productService.importFromXMLFile(file.getOriginalFilename());
        } else {
            productService.importFromCSVFile(file.getOriginalFilename());
        }
        return ResponseEntity.ok("success");
    }

    /**
     * Метод-сепаратор, возвращающий расширение файла
     * @param myFileName имя файла
     */
    private static String getFileExtension(String myFileName) {
        int index = myFileName.indexOf('.');
        return index == -1 ? null : myFileName.substring(index);
    }

    /**
     * Метод выводит список всех товаров
     * @return List<Product> возвращает список товаров
     */
    @GetMapping(value = "/rest/products/allProducts")
    public List<Product> findAll() {
        return productService.findAll();
    }

    /**
     * Метод возвращает список неудаленных товаров
     * @return List<Product> возвращает список товаров
     */
    @GetMapping(value = "/rest/products/getNotDeleteProducts")
    public List<Product> getNotDeleteProducts() {
        return productService.getNotDeleteProducts();
    }

    /**
     * Метод, ищет товар по id
     * @param productId идентификатор товара
     * @return Optional<Product> возвращает товар
     */
    @GetMapping(value = "/rest/products/{id}")
    public Optional<Product> findProductById(@PathVariable("id") Long productId) {
        return productService.findProductById(productId);
    }

    /**
     * Метод добавляет товар
     * @param product товар для добавления
     * @return ResponseEntity<Product> Возвращает добавленный товар с кодом ответа
     */
    @PostMapping(value = "/rest/products/addProduct/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> addProduct(@RequestBody Product product, @PathVariable Long id) {

        if (product.getProduct().equals("")) {
            log.debug("EmptyProductName");
            return new ResponseEntity("EmptyProductName", HttpStatus.BAD_REQUEST);
        }

        if (productService.existsProductByProduct(product.getProduct())) {
            log.debug("Product with name: {} already exists", product.getProduct());
            return new ResponseEntity("duplicatedNameProductError", HttpStatus.BAD_REQUEST);
        }

        productService.saveProduct(product);
        categoriesService.addToProduct(product, id);
        return ResponseEntity.ok(product);
    }

    /**
     * Редактирует товар
     * @param product товар для редактирования
     * @return ResponseEntity<Product> Возвращает отредактированный товар с кодом ответа
     */
    @PutMapping("/rest/products/editProduct")
    public ResponseEntity<Product> editProductM(@RequestBody Product product) {
        productService.editProduct(product);
        return ResponseEntity.ok(product);
    }

    /**
     * Редактирует товар и его категорию
     * @param product товар для редактирования
     */
    @PutMapping("/rest/products/editProduct/{idOld}/{idNew}")
    public ResponseEntity<Product> editProductAndCategory(@RequestBody Product product,
                                                          @PathVariable Long idOld,
                                                          @PathVariable Long idNew) {
        productService.editProduct(product);
        if (idOld == -1) {
            categoriesService.addToProduct(product, idNew);
        } else {
            categoriesService.removeFromProduct(product, idOld);
            categoriesService.addToProduct(product, idNew);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Метод удаления товара по идентификатору
     * @param id идентификатор товара
     */
    @DeleteMapping(value = "/rest/products/{id}")
    public ResponseEntity<Long> deleteProductById(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(id);
    }

    /**
     * Метод восстановления удаленного товара по идентификатору
     * @param id идентификатор товара
     */
    @PostMapping(value = "/rest/products/restoredeleted/{id}")
    public ResponseEntity<Long> restoreProductById(@PathVariable("id") Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok(id);
    }

    /**
     * Метод выбора продукта по категории
     *
     * @param categoryName - id выбранной категории
     * @return List<Product> отредактированный лист продуктов
     */

    @PutMapping(value = "/rest/products/{categoryName}")
    public List<Product> filterByCategory(@PathVariable String categoryName) {
        return productService.findProductsByCategoryName(categoryName);
    }

    /**
     * Метод выбора продукта по категории и сортирующий по возрастанию
     *
     * @param categoryName - id выбранной категории
     * @return List<Product> отредактированный лист продуктов
     */

    @GetMapping(value = "/rest/products/ascOrder/{categoryName}")
    public List<Product> filterByCategoryInAscOrder(@PathVariable String categoryName) {
        if (categoryName.equals("default")) {
            return productService.findAllOrderByRatingAsc();
        }
        return productService.findProductsByCategoryName(categoryName).stream()
                .sorted(Comparator.comparing(Product::getRating))

                .collect(Collectors.toList());
    }

    /**
     * Метод выбора продукта по категории и сортирующий по убыванию
     *
     * @param categoryName - id выбранной категории
     * @return List<Product> отредактированный лист продуктов
     */

    @GetMapping(value = "/rest/products/descOrder/{categoryName}")
    public List<Product> filterByCategoryInDescOrder(@PathVariable String categoryName) {
        if (categoryName.equals("default")) {
            return productService.findAllOrderByRatingDesc();
        }
        return productService.findProductsByCategoryName(categoryName).stream()
                .sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating()))
                .collect(Collectors.toList());
    }

    /**
     * Метод, который формирует файл с товарами нужной категории и передаёт обратно на страницу
     *
     * @param categoryName нужная категория товаров
     * @param response запрос для возврата информации
     * @return запрос с файлом xlsx
     */

    @GetMapping("/rest/products/report/{categoryName}/{number}/{orderSelect}")
    public ResponseEntity<FileSystemResource> getProductsReportAndExportToXlsx(@PathVariable String categoryName,
                                                                               @PathVariable Long number,
                                                                               @PathVariable String orderSelect,
                                                                               HttpServletResponse response) {
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            List<Product> products;
            if (categoryName.equals("default")) {
                if (orderSelect.equals("descOrder")) {
                    products = productService.findAllOrderByRatingDesc().stream().limit(number).collect(Collectors.toList());
                } else {
                    products = productService.findAllOrderByRatingAsc().stream().limit(number).collect(Collectors.toList());
                }
            } else {
                if (orderSelect.equals("descOrder")) {
                    products = productService.findProductsByCategoryName(categoryName).stream()
                            .sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating())).limit(number)
                            .collect(Collectors.toList());
                } else {
                    products = productService.findProductsByCategoryName(categoryName).stream()
                            .sorted(Comparator.comparing(Product::getRating)).limit(number)
                            .collect(Collectors.toList());
                }
            }
            response.setHeader("Size", String.valueOf(products.size()));
            productService.createXlsxDoc(products, categoryName).write(response.getOutputStream());
            return ResponseEntity.ok().build();
        } catch (NullPointerException | IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
