package com.jm.online_store.controller.rest;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.SharedStockDto;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ApiOperation(value = "Adds new sharedStock",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<SharedStockDto>> addSharedStock(@RequestBody SharedStock sharedStock) {
        sharedStock.setUser(userService.getCurrentLoggedInUser());
        sharedStockService.addSharedStock(sharedStock);
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(sharedStockService.addSharedStock(sharedStock), SharedStockDto.class)), HttpStatus.OK);
    }
}
