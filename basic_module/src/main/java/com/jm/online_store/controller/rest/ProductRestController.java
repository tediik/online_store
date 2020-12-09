package com.jm.online_store.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Рест контроллер страницы продукта.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;
    private final UserService userService;

    /**
     * Ищет продукт в БД по id из пути
     *
     * @param id продукта
     * @return сущность ProductDto, если продукт с таким id существует
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        ResponseEntity<ProductDto>[] answer = new ResponseEntity[1];
        User user = userService.getCurrentLoggedInUser();
        productService.getProductDto(id, user).ifPresentOrElse(
                value -> answer[0] = ResponseEntity.ok(value), () -> answer[0] = ResponseEntity.notFound().build());
        return answer[0];
    }

    /**
     * контроллер для получения Map содержащим ключ-значение: дата.время изменения цены - цена.
     *
     * @param id идентификатор продукта
     * @return map содержащая значения по изменению цены на товар.
     */
    @PostMapping("/productChangeMonitor")
    public ResponseEntity<Map> priceMonitor(@RequestBody Long id) {
        return ResponseEntity.ok(productService.getProductPriceChange(id));
    }

    /**
     * контроллер для получения обновлённого рейтинга товара
     *
     * @param rating оценка пользователя
     * @param id     id товара
     */
    @PostMapping("/rating")
    public ResponseEntity getNewRating(@RequestParam(value = "rating", required = false) float rating,
                                       @RequestParam(value = "id", required = false) Long id) {
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(productService.changeProductRating(id, rating, user));
    }

    /**
     * Mapping for search in {@link Product} by name contains search string
     *
     * @param searchString - {@link String} string to find in product name
     * @return - {@link ResponseEntity} response entity with List of {@link Product} or
     * if there are no such products returns notFound
     */
    @GetMapping("/searchByName/{searchString}")
    public ResponseEntity<List<Product>> findProductsByName(@PathVariable String searchString) {
        return ResponseEntity.ok(productService.findProductsByNameContains(searchString));
    }

    /**
     * Mapping for search in {@link Product} by description contains search string
     *
     * @param searchString - {@link String} string to find in product name
     * @return - {@link ResponseEntity} response entity with List of {@link Product} or
     * if there are no such products returns notFound
     */
    @GetMapping("/searchByDescription/{searchString}")
    public ResponseEntity<List<Product>> findProductsByDescription(@PathVariable String searchString) {
        return ResponseEntity.ok(productService.findProductsByDescriptionContains(searchString));
    }

    /**
     * Метод для добавления нового email в лист рассылок
     *
     * @param body тело запроса
     * @return ResponseEntity<String> со статусом ответа
     */
    @PostMapping("/subscribe")
    public ResponseEntity<String> addNewSubscriber(@RequestBody ObjectNode body) {
        if (productService.addNewSubscriber(body)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
        }
    }
}