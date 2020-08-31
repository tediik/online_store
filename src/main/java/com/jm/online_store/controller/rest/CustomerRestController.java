package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController("/api/customer")
public class CustomerRestController {
    private final StockService stockService;

    @GetMapping("/stock/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        if (stockService.findStockById(id).isEmpty()) {
            log.debug("Stock with id {} not exist", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Stock stock = stockService.findStockById(id).get();
        return new ResponseEntity<>(stock, HttpStatus.OK);
    }
}
