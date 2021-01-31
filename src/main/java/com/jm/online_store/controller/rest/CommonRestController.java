package com.jm.online_store.controller.rest;

import com.jm.online_store.model.StoreName;
import com.jm.online_store.repository.StoreNameRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * Контроллер для получения наименования магазина из БД
     * @return ResponseEntity<String> наименование магазина
     */

    @GetMapping("/storeName")
    @ApiOperation(value = "Get store name")
    public String getStoreName() {
        return storeNameRepository.findById(1).toString();
    }

    @PutMapping("/editStoreName")
    @ApiOperation(value = "edit store name")
    public void editStoreName(StoreName name){
        name.setId(1);
        System.out.println(name.toString());
        System.out.println(name.getId());
        storeNameRepository.save(name);
        storeNameRepository.flush();
    }
}
