package com.jm.online_store.controller.rest;


import com.jm.online_store.model.Product;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @param name имя файла, выбранное для сохранения
     * @param file файл с данными
     */

    @PostMapping(value = "/rest/products/uploadProductsFile")
    public void handleFileUpload(@RequestParam("name") String name,
                                 @RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File("uploads/xml/" + name + ".xml")));
            stream.write(bytes);
            stream.close();
        } catch (Exception e) {
            log.error("Ошибка сохранения файла");
            e.printStackTrace();
        }
        importFromFile(name);
//        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Метод импортирует список товаров из сохраненного XML файла
     * Записывает товары в БД
     */
    public void importFromFile(String fileName) {
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse("uploads/xml/" + fileName + ".xml");

            document.getDocumentElement().normalize();

            System.out.println("Root element :" + document.getDocumentElement().getNodeName());

            NodeList nList = document.getElementsByTagName("product");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String productName = eElement.getElementsByTagName("productname").item(0).getTextContent();
                    String productPrice = eElement.getElementsByTagName("price").item(0).getTextContent();
                    String productAmount = eElement.getElementsByTagName("amount").item(0).getTextContent();
                    System.out.println("Product : " + productName);
                    System.out.println("Price : " + productPrice);
                    System.out.println("Amount : " + productAmount);
                    Product product = new Product(productName, Double.parseDouble(productPrice), Integer.parseInt(productAmount));
                    productService.saveProduct(product);
                }
            }


        } catch (ParserConfigurationException e) {
            log.error("Ошибка конфигурации парсера");
            e.printStackTrace();
        } catch (SAXException e) {
            log.error("Ошибка XML синтаксиса");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Ошибка ввода/вывода");
            e.printStackTrace();
        }
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
    @PostMapping(value = "/rest/products/addProduct", consumes = "application/json")
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
