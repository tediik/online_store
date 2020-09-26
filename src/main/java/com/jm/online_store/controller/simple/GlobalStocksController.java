package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/global")
public class GlobalStocksController {

    //временная заглушка пока нет акций на главной
    @GetMapping("/stockDetails")
    public String showStockDetailsPage(){
        return "stockGlobalPage";
    }
}
