package com.jm.online_store.controller.simple;

import com.jm.online_store.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/manager")
public class StocksController {

    private final StockService stockService;

    @Autowired
    public StocksController(StockService stockService) {
        this.stockService = stockService;
    }


    //Вывод всех акций на странице
    @GetMapping("/stocks")
    public String getStocks() {

        return "stocks";
    }
}
