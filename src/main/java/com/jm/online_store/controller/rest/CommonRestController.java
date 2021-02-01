package com.jm.online_store.controller.rest;

import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.service.interf.CommonSettingsService;
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

    private CommonSettingsService commonSettingsService;

    @Autowired
    public CommonRestController(CommonSettingsService commonSettingsService){
        this.commonSettingsService = commonSettingsService;
    }

    /**
     * Контроллер для получения наименования магазина из БД
     * @return ResponseEntity<String> наименование магазина
     */

    @GetMapping("/storeName")
    @ApiOperation(value = "Get store name")
    public String getStoreName() {
        return commonSettingsService.getSettingByName("store_name").getTextValue();
    }

    @PutMapping("/editStoreName")
    @ApiOperation(value = "edit store name")
    public void editStoreName(CommonSettings commonSettings){
        commonSettingsService.updateTextValue(commonSettings);
    }
}
