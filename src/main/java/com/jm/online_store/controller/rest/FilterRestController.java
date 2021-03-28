package com.jm.online_store.controller.rest;

import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.filter.Filters;
import com.jm.online_store.service.interf.FilterService;
import com.jm.online_store.util.Transliteration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Rest controller for filters
 */
@Controller
@AllArgsConstructor
@RestController
@RequestMapping("/api/filter")
@Api(value = "Rest controller for filtering goods")
public class FilterRestController {

    private final FilterService filterService;

    /**
     * Возвращает сущность Filters, которые включают наборы данных для панели с фильтрами
     * @param category - String - категория товаров
     * @return ResponseEntity<ResponseDto < Filters>> - объект {@link Filters} с набором данных для заполнение полей фильтров
     */
    @GetMapping("/all/{category}")
    @ApiOperation(value = "return data for filters",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Data for filters found"),
            @ApiResponse(code = 400, message = "Data for filters was not found, wrong category name")
    })
    public ResponseEntity<ResponseDto<Filters>> getFilters(@PathVariable String category) {
        return new ResponseEntity<>(new ResponseDto<>(true, filterService.getFilters(category)), HttpStatus.OK);
    }

    /**
     * Осуществляет фильтрацию товаров в соответствии с выбранными фильтрами
     * @param category         - String - категория товара
     * @param labels           - Map<String, String> - набор характеристик для фильтрации. Ключ - название характеристики,
     *                         значение - значения для фильтрации
     * @return ResponseEntity<ResponseDto<List<ProductDto>>> {@link ProductDto} список продуктов, удовлетворяющих фильтрам
     */
    @GetMapping("/filtered/{category}")
    @ApiOperation(value = "return list of products by filters",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "products by filters were found"),
            @ApiResponse(code = 400, message = "products by filters cannot be found")
    })
    public ResponseEntity<ResponseDto<List<ProductDto>>> filterProduct(@PathVariable String category,
                                                                       @RequestParam(required = false) Map<String, String> labels) {
        return new ResponseEntity<>(new ResponseDto<>(true, filterService.getProductsByFilter(category, labels)), HttpStatus.OK);
    }
}