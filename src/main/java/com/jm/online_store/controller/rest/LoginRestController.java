package com.jm.online_store.controller.rest;

import com.jm.online_store.config.security.Twitter.TwitterAuth;
import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import com.jm.online_store.config.security.vk.VkApiClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@AllArgsConstructor
public class LoginRestController {

    private final OAuth2Odnoklassniki oAuth2Odnoklassniki;
    private final VkApiClient vkApiClient;
    private final TwitterAuth twitterAuth;

    /**
     * Контроллер для вызова метода по нажатию
     * кнопки "Зарегестрироваться через Одноклассники".
     * Вызов контроллера происходит в login.js
     */
    @GetMapping(value = "/login/odnoklassniki")
    public ResponseEntity<String> odnoklassnikiBtn() {
        return ResponseEntity.ok(oAuth2Odnoklassniki.getAuthorizationUrl());
    }

    /**
     * Контроллер для вызова метода по нажатию
     * кнопки "Зарегестрироваться через Вконтакте".
     * Вызов контроллера происходит в login.js
     */
    @GetMapping(value = "/login/vkontakte")
    public ResponseEntity<String> vkBtn() {
        return ResponseEntity.ok(vkApiClient.getVkAuthUrl());
    }

    /**
     * Контроллер для вызова метода по нажатию
     * кнопки "Зарегестрироваться через Твиттер".
     * Вызов контроллера происходит в login.js
     */
    @GetMapping(value = "/login/twitter")
    public ResponseEntity<String> twitterBtn() throws InterruptedException, ExecutionException, IOException {
        return ResponseEntity.ok(twitterAuth.twitterAuth());
    }
}


