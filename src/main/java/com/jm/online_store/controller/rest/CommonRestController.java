package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CommonSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(description = "Rest controller for transferring common data")
public class CommonRestController {

    private final CommonSettingsService commonSettingsService;

    /**
     * Метод для получения наименования магазина из БД
     * @return ResponseEntity<String> наименование магазина
     */
    @GetMapping("/storeName")
    @ApiOperation(value = "Get store name")
    public ResponseEntity<ResponseDto<String>> getStoreName() {
        return new ResponseEntity<>(new ResponseDto<>(true,
                commonSettingsService.getSettingByName("store_name").getTextValue(), ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
        //return ResponseEntity.ok(commonSettingsService.getSettingByName("store_name").getTextValue());
    }
}
