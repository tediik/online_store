package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
     *
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
     *
     * @param myFileName имя файла
     */
    private static String getFileExtension(String myFileName) {
        int index = myFileName.indexOf('.');
        return index == -1 ? null : myFileName.substring(index);
    }

    /**
     * Метод выводит список всех товаров
     *
     * @return List<Product> возвращает список товаров
     */
    @GetMapping(value = "/rest/products/allProducts")
    public List<Product> findAll() {
        return productService.findAll();
    }

    /**
     * Метод возвращает список неудаленых товаров
     *
     * @return List<Product> возвращает список товаров
     */
    @GetMapping(value = "/rest/products/getNotDeleteProducts")
    public List<Product> getNotDeleteProducts() {
        return productService.getNotDeleteProducts();
    }

    /**
     * Метод, ищет акции по id
     *
     * @param productId идентификатор товара
     * @return Optional<Product> возвращает товар
     */
    @GetMapping(value = "/rest/products/{id}")
    public Optional<Product> findProductById(@PathVariable("id") Long productId) {
        return productService.findProductById(productId);
    }

    /**
     * Метод добавляет товар
     *
     * @param product акиця для добавления
     * @return ResponseEntity<Product> Возвращает добавленную акцию с кодом ответа
     */
    @PostMapping(value = "/rest/products/addProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> addProductM(@RequestBody Product product) {
        productService.saveProduct(product);
        return ResponseEntity.ok(product);
    }

    /**
     * Редактирует товар
     *
     * @param product товар для редактирования
     * @return ResponseEntity<Product> Возвращает отредактированный товар с кодом ответа
     */
    @PutMapping("/rest/products/editProduct")
    public ResponseEntity<Product> editProductM(@RequestBody Product product) {
        productService.editProduct(product);
        return ResponseEntity.ok(product);
    }

    /**
     * Метод удаления товара по идентификатору
     *
     * @param id идентификатор товара
     */
    @DeleteMapping(value = "/rest/products/{id}")
    public ResponseEntity<Long> deleteProductById(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(id);
    }

    /**
     * Метод восстановления удаленного товара по идентификатору
     *
     * @param id идентификатор товара
     */
    @PostMapping(value = "/rest/products/restoredeleted/{id}")
    public ResponseEntity<Long> restoreProductById(@PathVariable("id") Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok(id);
    }

    /**
     * Метод, который формирует файл с товарами нужной категории и передаёт обратно на страницу
     *
     * @param category нужная категория товаров
     * @param response запрос для возврата информации
     * @return запрос с файлом xlsx
     */
    @GetMapping("/manager/products/report")
    public ResponseEntity<FileSystemResource> getProductsReportAndExportToXlsx(@RequestParam String category, HttpServletResponse response) {
        List<Product> productsList = new ArrayList<>(productService.findAll());
        if (!category.equals("all")) {
            productsList.removeIf(nextProduct -> !nextProduct.getProductType().getCategory().equals(category));
        }
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Products report");
            int rowCount = 0;

            XSSFRow row = sheet.createRow(rowCount++);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue("ID");
            cell = row.createCell(1);
            cell.setCellValue("Название");
            cell = row.createCell(2);
            cell.setCellValue("Цена");
            cell = row.createCell(3);
            cell.setCellValue("Количество");
            cell = row.createCell(4);
            cell.setCellValue("Рейтинг");
            cell = row.createCell(5);
            cell.setCellValue("Категория");

            for (Product aProduct : productsList) {
                row = sheet.createRow(rowCount++);

                cell = row.createCell(0);
                cell.setCellValue(aProduct.getId());

                cell = row.createCell(1);
                cell.setCellValue(aProduct.getProduct());

                cell = row.createCell(2);
                cell.setCellValue(aProduct.getPrice());

                cell = row.createCell(3);
                cell.setCellValue(aProduct.getAmount());

                cell = row.createCell(4);
                cell.setCellValue(aProduct.getRating());

                cell = row.createCell(5);
                cell.setCellValue(aProduct.getProductType().getCategory());
            }
            response.setHeader("Size", String.valueOf(rowCount-1));
            workbook.write(response.getOutputStream());
            return ResponseEntity.ok().build();
        } catch (NullPointerException | IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
