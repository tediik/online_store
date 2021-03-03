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
import org.springframework.http.HttpStatus;
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
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@Api(description = "Rest controller for product picture")
@RequestMapping("/api/product")  // Закрыл этот api для всех кроме ROLE_MANAGER
public class PictureProductController {

    private final ProductService productService;
    private static final String uploadDirectory = System.getProperty("user.dir") + File.separator + "uploads"
            + File.separator + "images" + File.separator + "products" + File.separator;
    // Переменная для хранения пути загрузки картинки
    // путь загрузки картинки выглядит так: ../uploads/images/products/{Имя файла картинки}
    private static final String loadPictureFrom = ".." + File.separator + "uploads" + File.separator + "images" + File.separator + "products" + File.separator;

    /**
     * Метод для добавления картинки
     *
     * @param id товара чью картинку меняем
     * @param pictureFile добавляемая картинка
     */
    @PutMapping("/upload/picture/{id}")
    @ApiOperation(value = "Upload picture for product by id and save path of picture in db",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Picture has been updated"),
            @ApiResponse(code = 400, message = "Image was not added")
    })
    public ResponseEntity<ResponseDto<String>> editPicture(@PathVariable("id") Long id, @RequestParam("pictureFile") MultipartFile pictureFile) {
        System.out.println("Добавляем");
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        String uniqueFilename = StringUtils.cleanPath(UUID.randomUUID() + "." + pictureFile.getOriginalFilename());
        List<String> productPictureNames = product.getProductPictureNames();
        if (!(pictureFile.isEmpty())) {
            Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
            try {
                if (!Files.exists(fileNameAndPath)) {
                    Files.createDirectories(fileNameAndPath.getParent());
                }
                byte[] bytes = pictureFile.getBytes();
                Files.write(fileNameAndPath, bytes);
                productPictureNames.remove(loadPictureFrom + "defaultPictureProduct.jpg");
                productPictureNames.add(loadPictureFrom + uniqueFilename);
            } catch (IOException e) {
                log.debug("Failed to store file: {}, because: {}", fileNameAndPath, e.getMessage());
            }
        }
        productService.editProduct(product);
        return ResponseEntity.ok(new ResponseDto<>(true, "uploads" + File.separator + "images"
                + File.separator + "products" + uniqueFilename, ResponseOperation.NO_ERROR.getMessage()));
    }

    /**
     * Метод для удаления картинки, если картинок больше нет она меняется на дефолтную
     * @param id id картинки, которую удаляем
     */
    @DeleteMapping("/picture/delete/{idPicture}") //Передавать 1 аргумент
    @ApiOperation(value = "Delete picture product by id from db and Directory",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Picture has been deleted"),
            @ApiResponse(code = 400, message = "Picture hasn't been deleted")
    })
    public ResponseEntity<ResponseDto<String>> deletePicture(@PathVariable("idPicture")Long id) {
        Path fileNameAndPath = null;
        Long productId = null;
        try {
            productId = productService.findProductIdByIdPicture(id);
            fileNameAndPath = Paths.get(uploadDirectory, productService.findProductPictureNamesById(id));
            Files.delete(fileNameAndPath);
        } catch (NullPointerException e){
            return new ResponseEntity<>(new ResponseDto<>(false, "файла с id " + id + " не существует" ), HttpStatus.BAD_REQUEST);
        } catch (IOException e){
            log.debug("Failed to delete file: {}, because: {} ", fileNameAndPath.getFileName().toString(), e.getMessage());
        }
        productService.deleteProductPictureNameById(id);
        if(productService.countAllPictureProductById(productId) == 0L){
           productService.saveProduct(productService.getProductById(productId));
        }
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format("picture with id " + ResponseOperation.HAS_BEEN_DELETED.getMessage(), id), ResponseOperation.NO_ERROR.getMessage()));
    }
}
