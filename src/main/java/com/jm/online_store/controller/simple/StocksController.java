package com.jm.online_store.controller.simple;

import com.jm.online_store.model.News;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Контроллер для отображения страницы акций в кабинете менеджера
 */
@Controller
//@RequestMapping("/manager")
@RequestMapping("/stockDetails")
@AllArgsConstructor
public class StocksController {
    private final StockService stockService;
    @GetMapping
    public String stockPage(Model model) {
        List<Stock> stocks = stockService.findAll();
        model.addAttribute("stocks", stocks);
        return "stocks";
    }

    @GetMapping("/{id}")
    public String stockDetails(@PathVariable(value = "id") Long id, Model model) {

        if (stockService.findStockById(id) == null) {
            return "redirect:/stockDetails";
        }
        Stock stock = stockService.findStockById(id);
        model.addAttribute("stock", stock);
        return "stockDetailsPage";
    }

    /**
     * Метод выводит страницу акций
     * @return возвращает страницу акций
     */
    @GetMapping("/stocks")
    public String getStocks() {
        return "stocks";
    }
}
