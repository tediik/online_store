package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.SharedStockDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.service.interf.SharedStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/manager/sharedStock")
@Api(description = "Rest controller for manage of shared stocks from manager page")
public class ManagerSharedStockRestController {
    private final SharedStockService sharedStockService;
    private final ModelMapper modelMapper;
    private final Type listType1 = new TypeToken<List<SharedStockDto>>() {}.getType();
    private final Type listType2 = new TypeToken<List<StockDto>>() {}.getType();

    /**
     * Метод добавляет сущность в базу данных
     *
     * @param sharedStockReq
     * @return  возвращает добавленную сущность
     */
    @PostMapping
    @ApiOperation(value = "Add shared stock",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Shared stock has been created"),
            @ApiResponse(code = 404, message = "Shared stock hasn't been found"),
            @ApiResponse(code = 404, message = "User hasn't been not found")
    })
    public ResponseEntity<ResponseDto<SharedStockDto>> addSharedStock(@RequestBody SharedStockDto sharedStockReq) {
        SharedStock gotBack = sharedStockService.addSharedStock(modelMapper.map(sharedStockReq, SharedStock.class));
        SharedStockDto returnValue = modelMapper.map(gotBack, SharedStockDto.class);
        returnValue.setStockDto(modelMapper.map(gotBack.getStock(), StockDto.class));
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.CREATED);
    }

    /**
     * Метод возвращает список типа SharedStock
     * или пустой список
     *
     * @return List<SharedStock> возвращает список всех SharedStock из базы данных
     */
    @GetMapping
    @ApiOperation(value = "Get all shared stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Shared stocks have been found"),
            @ApiResponse(code = 200, message = "Shared stocks haven't been found")
    })
    public ResponseEntity<ResponseDto<List<SharedStockDto>>> getQuantity() {
        //вытаскиваем список типа SharedStock из базы
        List<SharedStock> gotBack = sharedStockService.findAll();
        //получаем из списка типа SharedStock  список типа Stock
        List<Stock> stocksFromGotBack = gotBack.stream().map(SharedStock::getStock).collect(Collectors.toList());
        //маппим список типа Stock в тип StockDto
        List<StockDto> stocksDto = modelMapper.map(stocksFromGotBack, listType2);
        //маппим список типа SharedStock в тип SharedStockDto
        List<SharedStockDto> returnValue = modelMapper.map(sharedStockService.findAll(), listType1);
        //проходимся по списку типа SharedStockDto и сеттим в его элементы объекты из списка типа StockDto
        for (int i = 0; i < returnValue.size() ; i++) {
             returnValue.get(i).setStockDto(stocksDto.get(i));
        }
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }
}
