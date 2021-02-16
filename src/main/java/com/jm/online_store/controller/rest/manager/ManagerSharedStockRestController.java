package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.SharedStockDto;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Mode;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/manager/sharedStock")
@Api(description = "Rest controller for manage of shared stocks from manager page")
public class ManagerSharedStockRestController {
    private final SharedStockService sharedStockService;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    @ApiOperation(value = "Add shared stock",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<SharedStockDto>> addSharedStock(@RequestBody SharedStock sharedStock) {
        SharedStockDto returnValue;
        sharedStock.setUser(userService.getCurrentLoggedInUser());
        try {
            SharedStock sharedStockFromService = sharedStockService.addSharedStock(sharedStock);
            returnValue = modelMapper.map(sharedStockFromService, SharedStockDto.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    @GetMapping
    @ApiOperation(value = "Get all shared stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<SharedStockDto>>> getQuantity() {
        Type listType = new TypeToken<List<SharedStockDto>>() {}.getType();
        return ResponseEntity.ok(new ResponseDto<>(true , modelMapper.map(sharedStockService.findAll(), listType)));
    }
}
