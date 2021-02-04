package com.jm.online_store.controller.rest;

import com.jm.online_store.model.CurrentUrl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class LoginRestController {

    @PostMapping("/currentUrl")
    @ApiOperation(value = "deletes images",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity getCurrentUrl(@RequestBody String currentUrl) {
        CurrentUrl.setUrl(currentUrl);
        return ResponseEntity.ok().build();
    }
}
