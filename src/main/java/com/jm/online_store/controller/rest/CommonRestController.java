package com.jm.online_store.controller.rest;

import com.jm.online_store.repository.StoreNameRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private StoreNameRepository storeNameRepository;

    @Autowired
    public CommonRestController(StoreNameRepository storeNameRepository){
        this.storeNameRepository = storeNameRepository;
    }

    /**
     * Контроллер для получения наименования магазина из application.yml
     * @return ResponseEntity<String> наименование магазина
     */

    @GetMapping("/storeName")
    @ApiOperation(value = "Get store name")
    public String getStoreName() {
        return storeNameRepository.findById(1).toString();
    }
}
