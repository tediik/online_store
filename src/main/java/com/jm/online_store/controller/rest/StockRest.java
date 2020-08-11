package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Stock;
import com.jm.online_store.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController

public class StockRest {

    private final StockService stockService;

    @Autowired
    public StockRest(StockService stockService) {
        this.stockService = stockService;
    }


    //Вычисляем по id
    @GetMapping(value = "/rest/{id}")
    public Optional<Stock> findStockById(@PathVariable("id") Long id) {

        return stockService.findStockById(id);
    }

    //Добавляем новую акцию
    @PostMapping("/rest/addStock")
    public ResponseEntity<Stock> addStockM(@RequestBody Stock stock) {
        stockService.addStock(stock);

        return ResponseEntity.ok().body(stock);
    }




    //Редактируем акцию
    @PutMapping("/rest/editStock")
    public ResponseEntity<Stock> editStockM(@RequestBody Stock stock) {
        stockService.addStock(stock);

        return ResponseEntity.ok().body(stock);
    }



}
