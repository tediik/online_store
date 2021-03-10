package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.Response;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.model.dto.StockFilterDto;
import com.jm.online_store.service.interf.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/manager/stock")
@Api(description = "Rest controller for manage of stocks from manager page")
public class ManagerStockRestController {
    private final StockService stockService;
    private final Type listType = new TypeToken<List<StockDto>>() {}.getType();
    private final ModelMapper modelMapper;

    /**
     * Метод возвращает конкретный Stock по id
     * @param id
     * @return StockDto
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stock has been found"),
            @ApiResponse(code = 404, message = "Stock hasn't been found")
    })
    public ResponseEntity<ResponseDto<StockDto>> getStockById(@PathVariable Long id) {
        Stock requestedStock = stockService.findStockById(id);
        StockDto returnValue = modelMapper.map(requestedStock, StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    /**
     * Метод возвращает список всех Stock
     * или пустой список
     * @return List<StockDto>
     */
    @GetMapping("/allStocks")
    @ApiOperation(value = "Get list of all stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stocks have been found"),
            @ApiResponse(code = 200, message = "Stocks haven't been found")
    })
    public ResponseEntity<ResponseDto<List<StockDto>>> getAllStocks() {
        List<Stock> listStocksFromService = stockService.findAll();
        List<StockDto> returnValue = modelMapper.map(listStocksFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод возвращает страницу акций
     * @param page параметры страницы
     * @param filterDto параметры фильтра акции
     * @return Page<StockDto> возвращает страницу новостей
     */
    @GetMapping("/page")
    @ApiOperation(value = "Return stocks page",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Page has been found"),
            @ApiResponse(code = 404, message = "Page hasn't been found")
    })
    public ResponseEntity<ResponseDto<Page<StockDto>>> getStockPage(@PageableDefault Pageable page, StockFilterDto filterDto) {
        Page<Stock> stockPageFromService = stockService.findPage(page, filterDto);
        Page<StockDto> returnValue = modelMapper.map(stockPageFromService, Page.class);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    /**
     * Метод возвращает список текущих Stocks
     * или пустой список
     * @return List<StockDto> возвращает список StocksDto
     */
    @GetMapping("/currentStocks")
    @ApiOperation(value = "Get current stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Current stocks have been found"),
            @ApiResponse(code = 200, message = "Current stocks haven't been found")
    })
    public ResponseEntity<ResponseDto<List<StockDto>>> getCurrentStocks() {
        List<Stock> currentStocks = stockService.findCurrentStocks();
        List<StockDto> returnValue = modelMapper.map(currentStocks, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод возвращает список будущих Stocks
     * или пустой список
     * @return List<StockDto> возвращает список StocksDto
     */
    @GetMapping("/futureStocks")
    @ApiOperation(value = "Get future stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Future stocks  have been found"),
            @ApiResponse(code = 200, message = "Future stocks  haven't been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<StockDto>>> getFutureStocks() {
        List<Stock> futureStocksFromService = stockService.findFutureStocks();
        List<StockDto> returnValue = modelMapper.map(futureStocksFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод возвращает список прошлых Stocks
     * или пустой список
     * @return List<StockDto> возвращает список StocksDto
     */
    @GetMapping("/pastStocks")
    @ApiOperation(value = "Get past stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Past stocks have been found"),
            @ApiResponse(code = 200, message = "Past stocks haven't been found")
    })
    public ResponseEntity<ResponseDto<List<StockDto>>> getPastStocks() {
        List<Stock> currentStocks = stockService.findPastStocks();
        List<StockDto> returnValue = modelMapper.map(currentStocks, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод сохраняет сущность Stock в базу данных
     * @param stockReq
     * @return StockDto
     */
    @PostMapping
    @ApiOperation(value = "Add new stock",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 201, message = "Stock has been created")
    public ResponseEntity<ResponseDto<StockDto>> addNewStock(@RequestBody StockDto stockReq) {
        Stock gotBack = stockService.addStock(modelMapper.map(stockReq, Stock.class));
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(gotBack, StockDto.class)), HttpStatus.CREATED);
    }

    /**
     * Метод удаляет сущность Stock из базы данных по id
     * @param id - Long
     * @return String - описание резльтата операции
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stock has been deleted"),
            @ApiResponse(code = 404, message = "Stock hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> deleteStockById(@PathVariable Long id) {
        stockService.deleteStockById(id);
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format(Response.HAS_BEEN_DELETED.getText(), id, Response.NO_ERROR.getText())));
    }

    /**
     * Метод обновляет сущность Stock в базе данных
     * @param stockReq
     * @return StockDto - обновленную сущность из базы данных
     */
    @PutMapping
    @ApiOperation(value = "Method for update stock",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Stock has been updated"),
            @ApiResponse(code = 404, message = "Stock hasn't been found")
    })
    public ResponseEntity<ResponseDto<StockDto>> modifyStock(@RequestBody StockDto stockReq) {
        Stock gotBack = stockService.updateStock(modelMapper.map(stockReq, Stock.class));
        return ResponseEntity.ok(new ResponseDto<>(true, modelMapper.map(gotBack, StockDto.class)));
    }

}
