package com.jm.online_store.controller.rest;


import com.jm.online_store.model.Product;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для работы с добавлением/изменением товаров
 */
@RestController
@AllArgsConstructor
@Slf4j
public class ManagerProductsRestController {

    private final ProductService productService;

    /**
     * Метод обрабатывает загрузку файла с товарами на сервер
     * Вызывает соответствующий сервисный метод в зависимости от типа файла(CSV или XML)
     * @param file файл с данными
     */
    @PostMapping(value = "/rest/products/uploadProductsFile")
    public void handleFileUpload(@RequestParam("file") MultipartFile file) throws FileNotFoundException {
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
        if(getFileExtension(file.getOriginalFilename()).equals(".xml")){
            productService.importFromXMLFile(file.getOriginalFilename());
        }
            productService.importFromCSVFile(file.getOriginalFilename());
//        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Метод-сепаратор, возвращающий расширение файла
     * @param myFileName имя файла
     */
    private static String getFileExtension(String myFileName) {
        int index = myFileName.indexOf('.');
        return index == -1? null : myFileName.substring(index);
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
     * Метод, ищет акции по id
     * @param productId идентификатор товара
     * @return Optiona<Product> возвращает товар
     */
    @GetMapping(value = "/rest/products/{id}")
    public Optional<Product> findProductById(@PathVariable("id") Long productId) {
        return productService.findProductById(productId);
    }

    /**
     * Метод добавляет акцию
     * @param product акиця для добавления
     * @return ResponseEntity<Product> Возвращает добавленную акцию с кодом ответа
     */
    @PostMapping(value = "/rest/products/addProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Product> addProductM(@RequestBody Product product) {
        productService.saveProduct(product);
        return ResponseEntity.ok().body(product);
    }

    /**
     * Редактирует акцию
     * @param product акция для редактирования
     * @return ResponseEntity<Product> Возвращает отредактированный товар с кодом ответа
     */
    @PutMapping("/rest/products/editProduct")
    public ResponseEntity<Product> editProductM(@RequestBody Product product) {
        productService.saveProduct(product);
        return ResponseEntity.ok().body(product);
    }

    /**
     * Метод удаления акции по идентификатору
     * @param id идентификатор акции
     */
    @DeleteMapping(value = "/rest/products/{id}")
    public void deleteStockById(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }
}
