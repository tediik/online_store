package com.jm.online_store.controller.rest;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/manager/api/sharedStock")
@Api(description = "Rest controller for manage of shared stocks from manager page")
public class ManagerSharedStockRestController {
    private final SharedStockService sharedStockService;
    private final UserService userService;

    @PostMapping
    @ApiOperation(value = "Add shared stock")
    public ResponseEntity<String> addSharedStock(@RequestBody SharedStock sharedStock) {
        sharedStock.setUser(userService.getCurrentLoggedInUser());
        try {
            sharedStockService.addSharedStock(sharedStock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @ApiOperation(value = "Get all shared stocks")
    public ResponseEntity<List<SharedStock>> getQuantity() {
        return ResponseEntity.ok(sharedStockService.findAll());
    }
}
