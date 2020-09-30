package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Stock;
import com.jm.online_store.service.impl.StockServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping(value = "/stocks")
public class StocksController {

    private final StockServiceImpl stockService;

    @GetMapping
    public String stocksPage(Model model) {
        List<Stock> stocksPage = stockService.findAll();
        model.addAttribute("stocks", stocksPage);
        return "stocksPage";
    }

}
