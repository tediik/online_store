package com.jm.online_store.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.enums.Response;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Рест контроллер страницы продукта.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
@Api(description = "Rest controller for product page")
public class ProductRestController {
    private final ProductService productService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Type mapType = new TypeToken<Map<LocalDateTime, Double>>() {}.getType();
    private final Type listType = new TypeToken<List<ProductDto>>() {}.getType();
    /**
     * Ищет продукт в БД по id из пути
     *
     * @param id продукта
     * @return сущность ProductDto, если продукт с таким id существует
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product has been found"),
            @ApiResponse(code = 404, message = "Product has not been found")
    })
    public ResponseEntity<ResponseDto<ProductDto>> getProduct(@PathVariable Long id) {
        ResponseEntity<ProductDto>[] answer = new ResponseEntity[1];
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(new ResponseDto<>(true, productService.getProductDto(id, user).get()));
    }

    /**
     * контроллер для получения Map содержащим ключ-значение: дата.время изменения цены - цена.
     *
     * @param id идентификатор продукта
     * @return map содержащая значения по изменению цены на товар.
     */
    @PutMapping("/productChangeMonitor")
    @ApiOperation(value = "Get map with key: date of price changing; value: price",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<Map<LocalDateTime, Double>>> priceMonitor(@RequestBody Long id) {
        Map<LocalDateTime, Double> returnValue = modelMapper.map(productService.getProductPriceChange(id), mapType);
        return ResponseEntity.ok( new ResponseDto<>(true, returnValue));
    }

    /**
     * контроллер для обновления рейтинга товара
     *
     * @param rating оценка пользователя
     * @param id     id товара
     * @return - ResponseDto<Double> - обновленный рейтинг товара
     */
    @PostMapping("/rating")
    @ApiOperation(value = "Set new rating of product",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity <ResponseDto<Double>> getNewRating(@RequestParam(value = "rating", required = false) float rating,
                                       @RequestParam(value = "id", required = false) Long id) {
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(new ResponseDto<>(true, productService.changeProductRating(id, rating, user)));
    }

    /**
     * Mapping for search in {@link Product} by name contains search string
     *
     * @param searchString - {@link String} string to find in product name
     * @return - {@link ResponseEntity} response entity with List of {@link Product} or
     * if there are no such products returns notFound
     */
    @GetMapping("/searchByName/{searchString}")
    @ApiOperation(value = "Get product by name",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products have been found"),
            @ApiResponse(code = 404, message = "Products haven't been found")
    })
    public ResponseEntity<ResponseDto<List<ProductDto>>> findProductsByName(@PathVariable String searchString) {
        List<ProductDto> returnValue = modelMapper.map(productService.findProductsByNameContains(searchString),
                listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

//    /**
//     * Mapping for search in {@link Product} by description contains search string
//     *
//     * @param searchString - {@link String} string to find in product name
//     * @return - {@link ResponseEntity} response entity with List of {@link Product} or
//     * if there are no such products returns notFound
//     */
//    @GetMapping("/searchByDescription/{searchString}")
//    @ApiOperation(value = "Get product by description",
//            authorizations = { @Authorization(value = "jwtToken") })
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Products have been found"),
//            @ApiResponse(code = 404, message = "Products haven't been found")
//    })
//    public ResponseEntity<ResponseDto<List<ProductDto>>> findProductsByDescription(@PathVariable String searchString) {
//        List<ProductDto> returnValue = modelMapper.map(productService.findProductsByDescriptionContains(searchString),
//                listType);
//        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
//    }

    /**
     * Метод для добавления нового email в лист рассылок
     *
     * @param body тело запроса
     * @return ResponseEntity<String> со статусом ответа
     */
    @PostMapping("/subscribe")
    @ApiOperation(value = "Add a new subscriber by email",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 208, message = "Subscriber already exists")
    public ResponseEntity<ResponseDto<String>> addNewSubscriber(@RequestBody ObjectNode body) {
        if (productService.addNewSubscriber(body)) {
            return ResponseEntity.ok(new ResponseDto<>(true, Response.SUCCESS.getText()));
        } else {
            return ResponseEntity.ok(new ResponseDto<>(false, Response.FAILED.getText()));
        }
    }

    /**
     * Возвращает список первых count продуктов - count передаётся в метод сервиса .findNumProducts(count)
     *
     *  @param count колличество возвращаемых продуктов
     */
    @GetMapping("/first/{count}")
    @ApiOperation(value = "Returns a list with a given number of first products",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products have been found"),
            @ApiResponse(code = 404, message = "Products haven't been found")
    })
    public ResponseEntity<ResponseDto<List<ProductDto>>> getSomeProducts(@PathVariable Integer count) {
        List<ProductDto> returnValue = modelMapper.map(productService.findNumProducts(count),
                listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }
}
