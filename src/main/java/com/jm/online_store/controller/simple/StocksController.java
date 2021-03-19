package com.jm.online_store.controller.simple;

import com.jm.online_store.service.impl.StockServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping(value = "/stocks")
public class StocksController {

    @GetMapping
    public String stocksPage() {
        return "stocks-page";
    }

}
