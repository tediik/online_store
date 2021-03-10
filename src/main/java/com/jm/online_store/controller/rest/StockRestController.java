package com.jm.online_store.controller.rest;

import com.google.gson.Gson;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Рест контроллер для crud операций с акциями
 */
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@RestController
@AllArgsConstructor
@Api(description = "Rest controller for stocks")
@RequestMapping("/api/stock")
public class StockRestController {

    private final StockService stockService;

    /**
     * Возвращает список опубликованных акций на главную страницу
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/publishedstocks")
    @ApiOperation(value = "Returns list of published stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<Stock>> getPublishedStocks() {
        List<Stock> publishedStocks = stockService.findPublishedStocks();
        return ResponseEntity.ok(publishedStocks);
    }

    /**
     *  Загружает картинку для акции
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    @ApiOperation(value = "Method for upload and set image of stock",
            authorizations = { @Authorization(value = "jwtToken") })
    public void updateStockImage(@RequestParam("file") MultipartFile file) {
        stockService.updateStockImage(file);
    }

    /**
     * Метод выводит все акции
     *
     * @return List<Stock> список всех акций
     */
    @GetMapping(value = "/allStocks")
    @ApiOperation(value = "Get of all stocks")
    public List<Stock> findAll() {
        return stockService.findAll();
    }

    /**
     * Метод, ищет акции по id
     *
     * @param id идентификатор акции
     * @return Optiona<Stock> возвращает акцию
     */
    @PreAuthorize("permitAll()")
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public Stock findStockById(@PathVariable("id") Long id) {
        return stockService.findStockById(id);
    }

    /**
     * Метод добавляет акцию
     *
     * @param stock акиця для добавления
     * @return ResponseEntity<Stock> Возвращает добавленную акцию с кодом ответа
     */
    @PostMapping(value = "/addStock", consumes = "application/json")
    @ApiOperation(value = "Add a new stock",
            authorizations = { @Authorization(value = "jwtToken") })
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
    @PutMapping("/editStock")
    @ApiOperation(value = "Edit stock",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<Stock> editStockM(String stock) {
        Stock newStock = new Gson().fromJson(stock, Stock.class);
        stockService.addStock(newStock);
        return ResponseEntity.ok().body(newStock);
    }

    /**
     * Метод удаления акции по идентификатору
     *
     * @param id идентификатор акции
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    public void deleteStockById(@PathVariable("id") Long id) {
        stockService.deleteStockById(id);
    }
}
