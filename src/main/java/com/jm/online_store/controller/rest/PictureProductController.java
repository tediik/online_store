package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/api/product")
public class PictureProductController {

    private final ProductService productService;
    private static final String uploadDirectory = System.getProperty("user.dir") + File.separator + "src"
            + File.separator + "main" + File.separator + "resources"+ File.separator + "uploads"
            + File.separator + "images" + File.separator + "products";
    @PutMapping("/upload/picture/{id}")
    public void editPicture(@PathVariable("id")Long id, @RequestParam("pictureFile") MultipartFile pictureFile){

        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        String uniqueFilename = StringUtils.cleanPath(UUID.randomUUID() + "." + pictureFile.getOriginalFilename());
        if(!(pictureFile.isEmpty())){
            Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
            try {
                if(!Files.exists(fileNameAndPath)){
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
        System.out.println(pictureFile.getOriginalFilename());
        System.out.println(uniqueFilename);
    }
}
