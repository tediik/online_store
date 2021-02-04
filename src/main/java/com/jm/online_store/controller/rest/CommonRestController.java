package com.jm.online_store.controller.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(description = "Rest controller for transferring common data")
public class CommonRestController {

    @Value("${common-data.store-name}")
    private String storeName;

    /**
     * Контроллер для получения наименования магазина из application.yml
     * @return ResponseEntity<String> наименование магазина
     */

    @GetMapping("/storeName")
    @ApiOperation(value = "Get store name",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> getStoreName() {
        return ResponseEntity.ok(storeName);
    }
}
