package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class StockController {

    @GetMapping(value = "/stocks")
    public String stocks() {

        return "stocks";
    }
}
