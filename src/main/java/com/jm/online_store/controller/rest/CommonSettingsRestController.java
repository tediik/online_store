package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.CommonSettingsNotFoundException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CommonSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/commonSettings")
@AllArgsConstructor
@Api(description = "Rest controller for Common settings CRUD operations")
public class CommonSettingsRestController {
    private final CommonSettingsService commonSettingsService;

    /**
     * Exception handler method that catches all {@link CommonSettingsNotFoundException}
     * in current class and return ResponseEntity with not found status
     *
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler(CommonSettingsNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> commonSettingsNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "adds new common setting",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettings>> addNewSetting(@RequestBody CommonSettings commonSetting) {
        CommonSettings commonSettings = commonSettingsService.addSetting(commonSetting);
        return new ResponseEntity<>(new ResponseDto<>(true, commonSettings), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    @PutMapping
    @ApiOperation(value = "updates common settings",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettings>> updateSetting(@RequestBody CommonSettings commonSettings) {
        if (commonSettings.getSettingName().equals("maintenance_mode")) {
            commonSettingsService.updateMaintenanceMode(commonSettings);
        } else {
            commonSettingsService.updateTextValue(commonSettings);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, commonSettings), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    @GetMapping("/{settingName}")
        @ApiOperation(value = "get Common Setting by name",
                authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettings>> getCommonSettingByName(@PathVariable String settingName) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, commonSettingsService.getSettingByName(settingName)), HttpStatus.OK);
        //return ResponseEntity.ok(commonSettingsService.getSettingByName(settingName));
    }
}
