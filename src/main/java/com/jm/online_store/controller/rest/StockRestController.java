package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/stock")
public class StockRestController {
    private final StockService stockService;
    private final SharedStockService sharedStockService;

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        Stock requestedStock;
        try {
            requestedStock = stockService.findStockById(id);
        } catch (StockNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
//        return ResponseEntity.ok(requestedStock);
        return new ResponseEntity<>(requestedStock, HttpStatus.OK);
    }

}
