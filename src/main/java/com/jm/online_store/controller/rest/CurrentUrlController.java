package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.CurrentUrl;
import com.jm.online_store.model.dto.ResponseDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Рест контроллер для записи Url залогинившемуся пользователю.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor

public class CurrentUrlController {
    /**
     * Метод для записи Url залогинившемуся пользователю.
     */
    @PostMapping("/currentUrl")
    @ApiOperation(value = "Записывает Url залогинившемуся пользователю",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Url hasn't been set"),
            @ApiResponse(code = 200, message = "Url has been set"),
    })

    public ResponseEntity<ResponseDto<String>> getCurrentUrl(@RequestBody String currentUrl) {
        CurrentUrl.setUrl(currentUrl);
        return ResponseEntity.ok(new ResponseDto<>(true, ResponseOperation.SUCCESS.getMessage()));
    }
}
