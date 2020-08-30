package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для отображения страницы акций в кабинете менеджера
 */
@Controller
@RequestMapping("/manager")
@AllArgsConstructor
public class StocksController {
    private final StockService stockService;

    /**
     * Метод выводит страницу акций
     * @return возвращает страницу акций
     */
    @GetMapping("/stocks")
    public String getStocks() {
        return "stocks";
    }
}
