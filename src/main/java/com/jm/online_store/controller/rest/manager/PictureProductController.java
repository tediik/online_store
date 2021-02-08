package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@Api(description = "Rest controller for product picture")
@RequestMapping("/api/product")
public class PictureProductController {

    private final ProductService productService;
    private static final String uploadDirectory = System.getProperty("user.dir") + File.separator + "uploads"
            + File.separator + "images" + File.separator + "products";

    /**
     * Метод для изменения картинки
     * @param id товара чью картинку меняем
     * @param pictureFile добавляемая картинка
     */
    @ApiOperation(value = "Upload picture for product by id and save path of picture in db")
    @PutMapping("/upload/picture/{id}")
    public ResponseEntity<String> editPicture(@PathVariable("id") Long id, @RequestParam("pictureFile") MultipartFile pictureFile) {

        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        String uniqueFilename = StringUtils.cleanPath(UUID.randomUUID() + "." + pictureFile.getOriginalFilename());
        if (!(pictureFile.isEmpty())) {
            Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
            try {
                if (!Files.exists(fileNameAndPath)) {
                    Files.createDirectories(fileNameAndPath.getParent());
                }
                byte[] bytes = pictureFile.getBytes();
                Files.write(fileNameAndPath, bytes);
                product.setProduct_picture(uniqueFilename);
            } catch (IOException e) {
                log.debug("Failed to store file: {}, because: {}", fileNameAndPath, e.getMessage());
            }
        }
        productService.editProduct(product);
        return ResponseEntity.ok("uploads" + File.separator + "images"
                + File.separator + "products" + uniqueFilename);
    }

    /**
     * Метод для удаления картинки при этом картинка меняется на дефолтную
     * @param id товара чью картинку удаляем
     */
    @ApiOperation(value = "Delete picture product by id from db and Directory")
    @DeleteMapping("/picture/delete/{id}")
    public void deletePicture(@PathVariable("id") Long id) {
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        Path fileNameAndPath = Paths.get(uploadDirectory, product.getProduct_picture());
        try {
            if (!fileNameAndPath.getFileName().toString().equals("00.jpg")) {
                Files.delete(fileNameAndPath);
            }
        } catch (IOException e) {
            log.debug("Failed to delete file: {}, because: {} ", fileNameAndPath.getFileName().toString(), e.getMessage());
        }
        product.setProduct_picture("00.jpg");
        productService.editProduct(product);
    }
}
