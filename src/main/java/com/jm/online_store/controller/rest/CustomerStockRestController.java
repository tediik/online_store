package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/customer/api/stock")
public class CustomerStockRestController {
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
}
