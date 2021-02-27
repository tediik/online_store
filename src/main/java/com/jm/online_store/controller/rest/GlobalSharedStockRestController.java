package com.jm.online_store.controller.rest;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.dto.SharedStockDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/global/sharedStock")
public class GlobalSharedStockRestController {
    private final SharedStockService sharedStockService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ApiOperation(value = "Adds new sharedStock",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> addSharedStock(@RequestBody SharedStockDto sharedStockDto) {
        SharedStock sharedStock = modelMapper.map(sharedStockDto, SharedStock.class);
        sharedStock.setCustomer(customerService.getCurrentLoggedInCustomer());
        sharedStockService.addSharedStock(sharedStock);
        return ResponseEntity.ok().build();
    }
}
