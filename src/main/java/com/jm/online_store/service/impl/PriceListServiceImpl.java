package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.PriceListService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.scheduling.annotation.Scheduled;
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

@AllArgsConstructor
@Service
public class PriceListServiceImpl implements PriceListService {

    private static final String TIME_TO_START = "0 00 02 * * *";
    private static final String PATH_TO_XLS_FILE = "uploads/files/prices/pricedaily.xls";
    private static final String PATH_TO_ZIP_FILE = "uploads/files/prices/pricedaily.zip";

    private final ProductService productService;

    /**
     * Method creates XLS-file with the daily price list and archives it
     */
    @Scheduled(cron = TIME_TO_START)
    @Override
    public void createPrice() {

        //удаляем неактуальные файлы
        try {
            Files.deleteIfExists(Paths.get(PATH_TO_XLS_FILE));
            Files.deleteIfExists(Paths.get(PATH_TO_ZIP_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //инициализация виртуального xls-файла
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
        for (Product product : productsList) { //list.stream
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

        //записываем виртуальный файл в реальный
        try (FileOutputStream fos = new FileOutputStream(PATH_TO_XLS_FILE)){
            workbook.write(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //архивация
        archiveFileWithPrice();
    }

    public void archiveFileWithPrice() {

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(PATH_TO_ZIP_FILE));
             FileInputStream fis = new FileInputStream(new File(PATH_TO_XLS_FILE))){
            File zipFile = new File(PATH_TO_XLS_FILE);
            ZipEntry ze = new ZipEntry(zipFile.getName());
            zos.putNextEntry(ze);
            byte[] temp = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(temp)) > 0) {
                zos.write(temp, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
