package com.jm.online_store.controller.rest;

import com.google.gson.Gson;
import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.service.interf.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

import java.lang.reflect.Type;
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
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<ProductDto>>() {}.getType();

    /**
     * Возвращает список опубликованных акций на главную страницу
     *
     * @return List <StockDto> - list of published stocks
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/publishedstocks")
    @ApiOperation(value = "Returns list of published stocks",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Published stocks were not found"),
            @ApiResponse(code = 200, message = "Published stocks found")
    })
    public ResponseEntity<ResponseDto<List<StockDto>>> getPublishedStocks() {
        List<StockDto> publishedStocks = modelMapper.map(stockService.findPublishedStocks(), listType);
        return ResponseEntity.ok(new ResponseDto<>( true, publishedStocks));
    }

    /**
     * Загружает картинку для акции
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    @ApiOperation(value = "Method for upload and set image of stock",
            authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ResponseDto<String>> updateStockImage(@RequestParam("file") MultipartFile file) {
        stockService.updateStockImage(file);
        return ResponseEntity.ok(new ResponseDto<>(true, file.getName()));
    }

    /**
     * Метод выводит все акции
     *
     * @return List<Stock> список всех акций
     */
    @GetMapping(value = "/allStocks")
    @ApiOperation(value = "Get of all stocks")
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "All stocks were not found"),
            @ApiResponse(code = 200, message = "All stocks found")
    })
    public ResponseEntity<ResponseDto<List<StockDto>>> findAll() {
        List<StockDto> returnValue = modelMapper.map(stockService.findAll(), listType);
        return ResponseEntity.ok(new ResponseDto<>( true, returnValue));
    }

    /**
     * Метод, ищет акции по id
     *
     * @param id идентификатор акции
     * @return ResponseDto<StockDto> возвращает акцию
     */
    @PreAuthorize("permitAll()")
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Get stock by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Stock not found"),
            @ApiResponse(code = 200, message = "Stock found"),
            @ApiResponse(code = 204, message = "There is no stock with such id")
    })
    public ResponseEntity<ResponseDto<StockDto>> findStockById(@PathVariable("id") Long id) {
        StockDto returnValue = modelMapper.map(stockService.findStockById(id), StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод добавляет акцию
     *
     * @param stock акиця для добавления
     * @return ResponseDto<StockDto> Возвращает добавленную акцию с кодом ответа
     */
    @PostMapping(value = "/addStock", consumes = "application/json")
    @ApiOperation(value = "Add a new stock",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Stock not found"),
            @ApiResponse(code = 200, message = "Stock found")
    })
    public ResponseEntity<ResponseDto<StockDto>> addStockM(@RequestBody Stock stock) {
        StockDto returnValue = modelMapper.map(stockService.addStock(stock), StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Редактирует акцию
     *
     * @param stock акция для редактирования
     * @return ResponseEntity<Stock> Возвращает отредактированную акцию с кодом овтета
     */
    @PutMapping("/editStock")
    @ApiOperation(value = "Edit stock",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Stock not found"),
            @ApiResponse(code = 200, message = "Stock was successfully updated")
    })
    public ResponseEntity<ResponseDto<StockDto>> editStockM(String stock) {
        Stock newStock = new Gson().fromJson(stock, Stock.class);
        StockDto returnValue = modelMapper.map(stockService.addStock(newStock), StockDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод удаления акции по идентификатору
     *
     * @param id идентификатор акции
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete stock by ID",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Stock not found"),
            @ApiResponse(code = 200, message = "Stock was successfully deleted"),
            @ApiResponse(code = 204, message = "There is no stock with such id")
    })
    public ResponseEntity<ResponseDto<String>> deleteStockById(@PathVariable("id") Long id) {
        stockService.deleteStockById(id);
        return ResponseEntity.ok(new ResponseDto<>(true, String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(),
                id)));
    }
}