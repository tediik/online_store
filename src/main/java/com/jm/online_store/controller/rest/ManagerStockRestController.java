package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockFilterDto;
import com.jm.online_store.service.interf.StockService;
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
@RequestMapping("/manager/api/stock")
public class ManagerStockRestController {
    private final StockService stockService;

    @GetMapping("/{id}")
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
    public ResponseEntity<String> addNewStock(@RequestBody Stock stock) {
        stockService.addStock(stock);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStock(@PathVariable Long id) {
        stockService.deleteStockById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<String> modifyStock(@RequestBody Stock stock) {
        try {
            stockService.updateStock(stock);
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }


}
