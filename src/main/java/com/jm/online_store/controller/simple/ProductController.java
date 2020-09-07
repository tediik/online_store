package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    @GetMapping("/manager/productChangeMonitor")
    public String getProductChangeMonitor() {
        return "product";
    }

}
