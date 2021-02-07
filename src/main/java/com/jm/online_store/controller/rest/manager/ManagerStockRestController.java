package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockFilterDto;
import com.jm.online_store.service.interf.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/manager/stock")
@Api(description = "Rest controller for manage of stocks from manager page")
public class ManagerStockRestController {
    private final StockService stockService;

    @GetMapping("/{id}")
    @ApiOperation(value = "Get stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stock was not found")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        Stock requestedStock;
        try {
            requestedStock = stockService.findStockById(id);
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(requestedStock);
    }

    @GetMapping("/allStocks")
    @ApiOperation(value = "Get list of all stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> allStocks;
        try {
            allStocks = stockService.findAll();
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allStocks);
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
    public ResponseEntity<Page<Stock>> getStockPage(@PageableDefault Pageable page, StockFilterDto filterDto) {
        Page<Stock> stockPage;
        try {
            stockPage = stockService.findPage(page, filterDto);
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stockPage);
    }

    @GetMapping("/currentStocks")
    @ApiOperation(value = "Get current stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<List<Stock>> getCurrentStocks() {
        List<Stock> currentStocks;
        try {
            currentStocks = stockService.findCurrentStocks();
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currentStocks);
    }

    @GetMapping("/futureStocks")
    @ApiOperation(value = "Get future stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<List<Stock>> getFutureStocks() {
        List<Stock> futureStocks;
        try {
            futureStocks = stockService.findFutureStocks();
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(futureStocks);
    }

    @GetMapping("/pastStocks")
    @ApiOperation(value = "Get past stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stocks was not found")
    public ResponseEntity<List<Stock>> getPastStocks() {
        List<Stock> currentStocks;
        try {
            currentStocks = stockService.findPastStocks();
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currentStocks);
    }

    @PostMapping
    @ApiOperation(value = "Add new stock",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> addNewStock(@RequestBody Stock stock) {
        stockService.addStock(stock);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> deleteStock(@PathVariable Long id) {
        stockService.deleteStockById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @ApiOperation(value = "Method for update stock",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 404, message = "Stock was not found")
    public ResponseEntity<String> modifyStock(@RequestBody Stock stock) {
        try {
            stockService.updateStock(stock);
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }


}
