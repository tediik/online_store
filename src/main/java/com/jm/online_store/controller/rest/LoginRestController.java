package com.jm.online_store.controller.rest;

import com.jm.online_store.model.CurrentUrl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class LoginRestController {

    @PreAuthorize("permitAll()")
    @PostMapping("/currentUrl")
    @ApiOperation(value = "Return HttpStatus 200")
    public ResponseEntity getCurrentUrl(@RequestBody String currentUrl) {
        CurrentUrl.setUrl(currentUrl);
        return ResponseEntity.ok().build();
    }
}
