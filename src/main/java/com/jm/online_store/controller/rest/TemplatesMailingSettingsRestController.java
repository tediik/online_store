package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.TemplatesMailingSettingsNotFoundException;
import com.jm.online_store.model.TemplatesMailingSettings;
import com.jm.online_store.service.interf.TemplatesMailingSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/templatesMailingSettings")
@Api(description = "Rest controller for TemplatesMailingSettings CRUD operations")
public class TemplatesMailingSettingsRestController {
    private final TemplatesMailingSettingsService templatesMailingSettingsService;

    /**
     * Exception handler method that catches all {@link TemplatesMailingSettingsNotFoundException}
     * in current class and return ResponseEntity with not found status
     * @return - {@link ResponseEntity <String>}
     */
    @ExceptionHandler(TemplatesMailingSettingsNotFoundException.class)
    public ResponseEntity<String> templatesMailingSettingsNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }

    /**
     * updates TemplatesMailingSettings
     * @param templatesMailingSettings
     * @return ResponseEntity<TemplatesMailingSettings>
     */
    @PutMapping
    @ApiOperation(value = "updates TemplatesMailing settings",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 204, message = "TemplatesMailingSetting not update"),
            @ApiResponse(code = 200, message = "TemplatesMailingSetting was update")
    })
    public ResponseEntity<TemplatesMailingSettings> updateSetting(@RequestBody TemplatesMailingSettings templatesMailingSettings) {
        templatesMailingSettingsService.updateTextValue(templatesMailingSettings);
        return ResponseEntity.ok().body(templatesMailingSettings);
    }

    /**
     * Get TemplatesMailingSetting by name
     * @param settingName
     * @return ResponseEntity<TemplatesMailingSettings>
     */
    @GetMapping("/{settingName}")
    @ApiOperation(value = "get TemplatesMailingSetting by name",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 204, message = "TemplatesMailingSetting not found"),
            @ApiResponse(code = 200, message = "TemplatesMailingSetting was found")
    })
    public ResponseEntity<TemplatesMailingSettings> getTemplatesMailingSettings(@PathVariable String settingName) {
        return ResponseEntity.ok(templatesMailingSettingsService.getSettingByName(settingName));
    }
}
