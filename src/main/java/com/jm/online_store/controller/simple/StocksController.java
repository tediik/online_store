package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
@AllArgsConstructor
public class StocksController {
    private final StockService stockService;

    //Вывод всех акций на странице
    @GetMapping("/stocks")
    public String getStocks() {
        return "stocks";
    }
}
