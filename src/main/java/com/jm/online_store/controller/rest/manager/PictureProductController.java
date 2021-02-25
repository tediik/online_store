package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
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
            + File.separator + "images" + File.separator + "products" + File.separator;
    // Переменная для хранения пути загрузки картинки
    // путь загрузки картинки выглядит так: ../uploads/images/products/{Имя файла картинки}
    private static final String loadPictureFrom = ".." + File.separator + "uploads" + File.separator + "images" + File.separator + "products" + File.separator;

    /**
     * Метод для изменения картинки
     *
     * @param id товара чью картинку меняем
     * @param pictureFile добавляемая картинка
     */
    @PutMapping("/upload/picture/{id}")
    @ApiOperation(value = "Upload picture for product by id and save path of picture in db",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Picture has been updated"),
            @ApiResponse(code = 400, message = "Picture hasn't been updated")
    })
    public ResponseEntity<ResponseDto<String>> editPicture(@PathVariable("id") Long id, @RequestParam("pictureFile") MultipartFile pictureFile) {
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
                product.setProductPictureName(loadPictureFrom + uniqueFilename);
            } catch (IOException e) {
                log.debug("Failed to store file: {}, because: {}", fileNameAndPath, e.getMessage());
            }
        }
        productService.editProduct(product);
        return ResponseEntity.ok(new ResponseDto<>(true, "uploads" + File.separator + "images"
                + File.separator + "products" + uniqueFilename));
    }

    /**
     * Метод для удаления картинки при этом картинка меняется на дефолтную
     * @param id товара чью картинку удаляем
     */
    @DeleteMapping("/picture/delete/{id}")
    @ApiOperation(value = "Delete picture product by id from db and Directory",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Picture has been deleted"),
            @ApiResponse(code = 400, message = "Picture hasn't been deleted")
    })
    public ResponseEntity<ResponseDto<String>> deletePicture(@PathVariable("id") Long id) {
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        Path fileNameAndPath = Paths.get(uploadDirectory, product.getProductPictureName());
        try {
            if (!fileNameAndPath.getFileName().toString().equals(loadPictureFrom + "defaultPictureProduct.jpg")) {
                Files.delete(fileNameAndPath);
            }
        } catch (IOException e) {
            log.debug("Failed to delete file: {}, because: {} ", fileNameAndPath.getFileName().toString(), e.getMessage());
        }
        product.setProductPictureName(loadPictureFrom + "defaultPictureProduct.jpg");
        productService.editProduct(product);
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id)));
    }
}
