package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.CommonSettingsNotFoundException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.dto.CommonSettingsDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CommonSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    /**
     * Exception handler method that catches all {@link CommonSettingsNotFoundException}
     * in current class and return ResponseEntity<ResponseDto<String>> with not found status
     *
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler(CommonSettingsNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> commonSettingsNotFoundExceptionHandler() {
        return new ResponseEntity<>(new ResponseDto<>(false, "Common settings not found"), HttpStatus.NOT_FOUND);
        //return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "adds new common setting",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettingsDto>> addNewSetting(@RequestBody CommonSettings commonSetting) {
        return new ResponseEntity<>(new ResponseDto<>(true,
                modelMapper.map(commonSettingsService.addSetting(commonSetting), CommonSettingsDto.class)), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    @PutMapping
    @ApiOperation(value = "updates common settings",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettingsDto>> updateSetting(@RequestBody CommonSettings commonSettings) {
        if (commonSettings.getSettingName().equals("maintenance_mode")) {
            commonSettingsService.updateMaintenanceMode(commonSettings);
        } else {
            commonSettingsService.updateTextValue(commonSettings);
        }
        return new ResponseEntity<>(new ResponseDto<>(true,
                modelMapper.map(commonSettings, CommonSettingsDto.class)), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    @GetMapping("/{settingName}")
        @ApiOperation(value = "get Common Setting by name",
                authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettingsDto>> getCommonSettingByName(@PathVariable String settingName) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(commonSettingsService.getSettingByName(settingName), CommonSettingsDto.class)), HttpStatus.OK);
        //return ResponseEntity.ok(commonSettingsService.getSettingByName(settingName));
    }
}
