package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
/**
 * Рест контроллер для работы с изображением
 */
@RestController
@RequestMapping(value = "/api/image")
@RequiredArgsConstructor
@Api(description = "Rest controller for image")
public class ImageRestController {

    private final UserService userService;


    /**
     * Метод для загрузки картинки
     *
     * @param imageFile загружаемая картинка
     */
    @PostMapping("/upload")
    @ApiOperation(value = "uploads images",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image has been uploaded"),
            @ApiResponse(code = 400, message = "Image hasn't been uploaded")
    })
    public ResponseEntity<ResponseDto<String>> handleImagePost(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        User userDetails = userService.getCurrentLoggedInUser();
        userService.updateUserImage(userDetails.getId(), imageFile);
        return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.SUCCESS.getMessage()));

    }


    /**
     * Метод для удаления картинки
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "deletes images",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image has been deleted"),
            @ApiResponse(code = 400, message = "Image hasn't been deleted")
    })
    public ResponseEntity<ResponseDto<String>> deleteImage() throws IOException {
        User userDetails = userService.getCurrentLoggedInUser();
        userService.deleteUserImage(userDetails.getId());
        return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.HAS_BEEN_DELETED.getMessage()));
    }
}
