package com.jm.online_store.controller.rest;

import com.jm.online_store.config.security.Twitter.TwitterAuth;
import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import com.jm.online_store.config.security.vk.VkApiClient;
import com.jm.online_store.model.dto.ResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
@Api(description = "Rest controller for social network authorization")
@RequestMapping("/api")
public class Oauth2RestController {

    private final OAuth2Odnoklassniki oAuth2Odnoklassniki;
    private final VkApiClient vkApiClient;
    private final TwitterAuth twitterAuth;

    /**
     * Контроллер для вызова метода по нажатию
     * кнопки "Зарегестрироваться через Одноклассники".
     * Вызов контроллера происходит в login.js
     */
    @GetMapping(value = "/login/odnoklassniki")
    @ApiOperation(value = "Get authorization with odnoklassniki in login.js")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authorization was initialized successfully "),
            @ApiResponse(code = 400, message = "Authorization wasn't initialized "),
    })
    public ResponseEntity<ResponseDto<String>> odnoklassnikiBtn() {
        return ResponseEntity.ok(new ResponseDto<>(true, oAuth2Odnoklassniki.getAuthorizationUrl()));
    }

    /**
     * Контроллер для вызова метода по нажатию
     * кнопки "Зарегестрироваться через Вконтакте".
     * Вызов контроллера происходит в login.js
     */
    @GetMapping(value = "/login/vkontakte")
    @ApiOperation(value = "Get authorization with VK in login.js")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authorization was initialized successfully "),
            @ApiResponse(code = 400, message = "Authorization wasn't initialized "),
    })
    public ResponseEntity<ResponseDto<String>> vkBtn() {
        return ResponseEntity.ok(new ResponseDto<>(true, vkApiClient.getVkAuthUrl()));
    }

    /**
     * Контроллер для вызова метода по нажатию
     * кнопки "Зарегестрироваться через Твиттер".
     * Вызов контроллера происходит в login.js
     */
    @GetMapping(value = "/login/twitter")
    @ApiOperation(value = "Get authorization with twitter in login.js")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Authorization was initialized successfully "),
            @ApiResponse(code = 400, message = "Authorization wasn't initialized "),
    })
    public ResponseEntity<ResponseDto<String>> twitterBtn() throws InterruptedException, ExecutionException,
            IOException {
        return ResponseEntity.ok( new ResponseDto<>(true, twitterAuth.twitterAuth()));
    }
}


