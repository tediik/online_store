package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.PriceListService;
import com.jm.online_store.service.interf.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
@Service
public class PriceListServiceImpl implements PriceListService {

    @Value("${price-list-service.path-to-xls-file}")
    private String PATH_TO_XLS_FILE;
    @Value("${price-list-service.path-to-zip-file}")
    private String PATH_TO_ZIP_FILE;

    private final ProductService productService;

    public PriceListServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run() {
        createPrice();
    }

    /**
     * Method creates XLS-file with the daily price list and archives it
     */
    public void createPrice() {
        //удаляем неактуальные файлы
        try {
            Files.deleteIfExists(Paths.get(PATH_TO_XLS_FILE));
            Files.deleteIfExists(Paths.get(PATH_TO_ZIP_FILE));
        } catch (IOException e) {
            log.debug("Unable to delete daily pricelist .xls or .zip files for some reason");
        }

        //инициализация виртуального xls-файла
        HSSFWorkbook workbook = xlsFileInit();

        //записываем виртуальный файл в реальный
        try (FileOutputStream fos = new FileOutputStream(PATH_TO_XLS_FILE)){
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            log.debug("File not found -> failed to save .xls price list");
        } catch (IOException e) {
            log.debug("Failed to save .xls price list");
        }

        //архивация
        archiveFile(PATH_TO_XLS_FILE, PATH_TO_ZIP_FILE);
    }

    /**
     * Method creates virtual XLS-file with the daily price list
     */
    private HSSFWorkbook xlsFileInit() {
        int rowCount = 0;
        List<Product> productsList = productService.findAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("daily price list");

        //заголовок страницы
        Row row = sheet.createRow(rowCount);
        row.createCell(0).setCellValue("№");
        row.createCell(1).setCellValue("Наименование");
        row.createCell(2).setCellValue("Цена");
        row.createCell(3).setCellValue("Наличие");

        //заполняем страницу
        for (Product product : productsList) {
            row = sheet.createRow(++rowCount);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getProduct());
            row.createCell(2).setCellValue(product.getPrice());
            row.createCell(3).setCellValue(product.getAmount());
        }

        //дата актуализации
        row = sheet.createRow(++rowCount);
        row = sheet.createRow(++rowCount);
        row.createCell(3).setCellValue("created: ");
        row.createCell(4).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //придаем читабельность файлу
        IntStream.range(0,3).forEach(i -> sheet.autoSizeColumn(i));

        return workbook;
    }

    /**
     * Method archives any input file
     *
     * @param incomingFile - path to original file
     * @param outgoingFile - path to archived file
     */
    public void archiveFile(String incomingFile, String outgoingFile) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outgoingFile));
             FileInputStream fis = new FileInputStream(new File(incomingFile))) {
            File zipFile = new File(incomingFile);
            ZipEntry ze = new ZipEntry(zipFile.getName());
            zos.putNextEntry(ze);
            byte[] temp = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(temp)) > 0) {
                zos.write(temp, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            log.debug("File not found -> failed to save .zip price list");
        } catch (IOException e) {
            log.debug("Failed to save .zip price list");
        }
    }
}
