package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.Response;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/image")
@RequiredArgsConstructor
@Api(description = "Rest controller for image")
public class ImageRestController {

    private final UserService userService;

    @GetMapping
    @ApiOperation(value = "get images",
    authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Image not found"),
            @ApiResponse(code = 200, message = "Image was found")
    })
    public ResponseEntity<ResponseDto<String>> getImage() {
        return new ResponseEntity<>(new ResponseDto<>(
                true, userService.getCurrentLoggedInUser().getProfilePicture(),
                Response.NO_ERROR.getText()), HttpStatus.OK);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "uploads images",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Image not changed"),
            @ApiResponse(code = 200, message = "Image was changed")
    })
    public ResponseEntity<ResponseDto<String>> handleImagePost(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        return new ResponseEntity<>(new ResponseDto<>(
                true, userService.updateUserImage(userService.getCurrentLoggedInUser().getId(), imageFile),
                Response.NO_ERROR.getText()), HttpStatus.OK);

    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "deletes images",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Image not deleted"),
            @ApiResponse(code = 200, message = "Image was deleted")
    })
    public ResponseEntity<ResponseDto<String>> deleteImage() throws IOException {
        return new ResponseEntity<>(new ResponseDto<>(
                true, userService.deleteUserImage(userService.getCurrentLoggedInUser().getId()),
                Response.NO_ERROR.getText()), HttpStatus.OK);
    }
}
