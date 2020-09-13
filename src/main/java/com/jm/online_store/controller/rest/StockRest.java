package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Рест контроллер для crud операций с акциями
 */
@RestController
@AllArgsConstructor
public class StockRest {
    private final StockService stockService;

    /**
     * Метод выводит все акции
     *
     * @return List<Stock> список всех акций
     */
    @GetMapping(value = "/rest/allStocks")
    public ResponseEntity<Page<Stock>> findAll(@PageableDefault Pageable page) {
        LocalDate presentDate = LocalDate.now();
        Page<Stock> stocks = stockService.findAll(page);
        for (Stock stock : stocks) {
            if (((stock.getStartDate()) != null)) {
                if (presentDate.isBefore(stock.getStartDate())) {
                    stock.setStockType(Stock.StockType.FUTURE);
                } else if (stock.getEndDate() != null && presentDate.isAfter(stock.getEndDate())
                        || (presentDate.equals(stock.getEndDate()))) {
                    stock.setStockType(Stock.StockType.PAST);
                } else if ((presentDate.isAfter(stock.getStartDate()) || (presentDate.equals(stock.getStartDate()))
                        && stock.getEndDate() != null && (presentDate.isBefore(stock.getEndDate())))) {
                    stock.setStockType(Stock.StockType.CURRENT);
                } else {
                    stock.setStockType(Stock.StockType.CURRENT);
                }
            }
        }
        return new ResponseEntity<>(stocks, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * Метод, ищет акции по id
     *
     * @param id идентификатор акции
     * @return Optiona<Stock> возвращает акцию
     */
    @GetMapping(value = "/rest/{id}")
    public Stock findStockById(@PathVariable("id") Long id) {
        return stockService.findStockById(id);
    }

    /**
     * Метод добавляет акцию
     *
     * @param stock акиця для добавления
     * @return ResponseEntity<Stock> Возвращает добавленную акцию с кодом ответа
     */
    @PostMapping(value = "/rest/addStock", consumes = "application/json")
    public ResponseEntity<Stock> addStockM(@RequestBody Stock stock) {
        stockService.addStock(stock);
        return ResponseEntity.ok().body(stock);
    }

    /**
     * Редактирует акцию
     *
     * @param stock акция для редактирования
     * @return ResponseEntity<Stock> Возвращает отредактированную акцию с кодом овтета
     */
    @PutMapping("/rest/editStock")
    public ResponseEntity<Stock> editStockM(@RequestBody Stock stock) {
        stockService.addStock(stock);
        return ResponseEntity.ok().body(stock);
    }

    /**
     * Метод удаления акции по идентификатору
     *
     * @param id идентификатор акции
     */
    @DeleteMapping(value = "/rest/{id}")
    public void deleteStockById(@PathVariable("id") Long id) {
        stockService.deleteStockById(id);
    }
}
