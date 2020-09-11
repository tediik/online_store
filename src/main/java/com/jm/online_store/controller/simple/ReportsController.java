package com.jm.online_store.controller.simple;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для отображения страницы отчётов в кабинете менеджера
 */
@Controller
@RequestMapping("/manager")
@AllArgsConstructor
public class ReportsController {
    /**
     * Метод выводит страницу отчётов
     * @return возвращает страницу отчётов
     */
    @GetMapping("/reports")
    public String getReports() {
        return "reports";
    }
}
