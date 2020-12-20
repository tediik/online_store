package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.CommonSettingsNotFoundException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.service.interf.CommonSettingsService;
import lombok.AllArgsConstructor;
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
public class CommonSettingsRestController {
    private final CommonSettingsService commonSettingsService;

    /**
     * Exception handler method that catches all {@link CommonSettingsNotFoundException}
     * in current class and return ResponseEntity with not found status
     *
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler(CommonSettingsNotFoundException.class)
    public ResponseEntity<String> commonSettingsNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> addNewSetting(@RequestBody CommonSettings commonSetting) {
        commonSettingsService.addSetting(commonSetting);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<CommonSettings> updateSetting(@RequestBody CommonSettings commonSettings) {
        if (commonSettings.getSettingName().equals("maintenance_mode")) {
            commonSettingsService.updateMaintenanceMode(commonSettings);
        } else {
            commonSettingsService.updateTextValue(commonSettings);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{settingName}")
    public ResponseEntity<CommonSettings> getCommonSettingByName(@PathVariable String settingName) {
        return ResponseEntity.ok(commonSettingsService.getSettingByName(settingName));
    }
}
