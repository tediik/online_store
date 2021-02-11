package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.model.dto.StockFilterDto;
import com.jm.online_store.service.interf.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final ModelMapper modelMapper = new ModelMapper();
    private final Type listType = new TypeToken<List<StockDto>>() {}.getType();


    @GetMapping("/{id}")
    @ApiOperation(value = "Get stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stock was not found")
    public ResponseEntity<ResponseDto<StockDto>> getStockById(@PathVariable Long id) {
        Stock requestedStock = stockService.findStockById(id);
        StockDto returnValue = modelMapper.map(requestedStock, StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    @GetMapping("/allStocks")
    @ApiOperation(value = "Get list of all stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<ResponseDto<List<StockDto>>> getAllStocks() {
        List<Stock> listStocksFromService = stockService.findAll();
        List<StockDto> returnValue = modelMapper.map(listStocksFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод возвращает страницу акций
     *
     * @param page параметры страницы
     * @return Page<Stock> возвращает страницу новостей
     */
    @GetMapping("/page")
    @ApiOperation(value = "Return stocks page",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Page was not found")
    public ResponseEntity<ResponseDto<Page<StockDto>>> getStockPage(@PageableDefault Pageable page, StockFilterDto filterDto) {
        Page<Stock> stockPageFromService = stockService.findPage(page, filterDto);
        Page<StockDto> returnValue = modelMapper.map(stockPageFromService, Page.class);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    @GetMapping("/currentStocks")
    @ApiOperation(value = "Get current stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<ResponseDto<List<StockDto>>> getCurrentStocks() {
        List<Stock> currentStocks = stockService.findCurrentStocks();
        List<StockDto> returnValue = modelMapper.map(currentStocks, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    @GetMapping("/futureStocks")
    @ApiOperation(value = "Get future stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<ResponseDto<List<StockDto>>> getFutureStocks() {
        List<Stock> futureStocksFromService = stockService.findFutureStocks();
        List<StockDto> returnValue = modelMapper.map(futureStocksFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    @GetMapping("/pastStocks")
    @ApiOperation(value = "Get past stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<ResponseDto<List<StockDto>>> getPastStocks() {
        List<Stock> currentStocks = stockService.findPastStocks();
        List<StockDto> returnValue = modelMapper.map(currentStocks, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    @PostMapping
    @ApiOperation(value = "Add new stock",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<StockDto>> addNewStock(@RequestBody Stock stock) {
        Stock stockFromService = stockService.addStock(stock);
        StockDto returnValue = modelMapper.map(stockFromService, StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> deleteStock(@PathVariable Long id) {
        return stockService.deleteStockById(id) ?
                ResponseEntity.ok(new ResponseDto<>(true,
                        String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id)))
                : ResponseEntity.badRequest().build();
    }

    @PutMapping
    @ApiOperation(value = "Method for update stock",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stock was not found")
    public ResponseEntity<ResponseDto<StockDto>> modifyStock(@RequestBody Stock stock) {
        Stock stockFromService = stockService.updateStock(stock);
        StockDto returnValue = modelMapper.map(stockFromService, StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }


}
