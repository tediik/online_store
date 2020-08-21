package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotFoundPageController {

    @GetMapping("/404")
    public String getStocks() {
        return "404Page";
    }
}
