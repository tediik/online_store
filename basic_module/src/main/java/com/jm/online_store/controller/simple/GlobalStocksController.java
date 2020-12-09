package com.jm.online_store.controller.simple;

import com.jm.online_store.model.Stock;
import com.jm.online_store.service.impl.StockServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/global")
public class GlobalStocksController {

    private final StockServiceImpl stockService;

    @GetMapping("/stockDetails/{id}")
    public String showStockDetailsPage(@PathVariable(value = "id") Long id, Model model) {
        if (stockService.findStockById(id) == null) {
            return "redirect:/stocks";
        }
        Stock stock = stockService.findStockById(id);
        model.addAttribute("stock", stock);
        return "stockGlobalPage";
    }
}
