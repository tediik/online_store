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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/stocks")
public class StockRest {

    private final StockService stockService;

    @Autowired
    public StockRest(StockService stockService) {
        this.stockService = stockService;
    }


    //Выводим все акции
    @GetMapping()
    public List<Stock> findAllStocks() {

        return stockService.findAllStocks();
    }

    //Вычисляем по id
    @GetMapping(value = "/{id}")
    public Optional<Stock> findStockById(@PathVariable("id") Long id) {

        return stockService.findStockById(id);
    }

//    //Добавляем новую акцию
//    @PostMapping()
//    public void addStock() {
//        stockService.addStock();
//    }

    //Удаляем акцию по id
    @DeleteMapping(value = "/{stockId}")
    public ResponseEntity<Void> deleteStockById(@PathVariable Long stockId) {
        stockService.deleteStockById(stockId);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

//    //Редактируем новую акцию
//    @PutMapping()
//    public void updateStock() {
//        stockService.updateStock();
//
//    }

}
