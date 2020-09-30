package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.ProductsNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private ProductService productService;

    /**
     * Ищет продукт в БД по id из пути
     *
     * @param id продукта
     * @return сущность Product, если продукт с таким id существует
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        ResponseEntity<Product>[] answer = new ResponseEntity[1];
        productService.findProductById(id).ifPresentOrElse(
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
     * @param user   авторизованный пользователь
     */
    @PostMapping("/rating")
    public ResponseEntity getNewRating(@RequestParam(value = "rating", required = false) float rating,
                                       @RequestParam(value = "id", required = false) Long id,
                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(productService.changeProductRating(id, rating, user));
    }

    /**
     * Mapping for search in {@link Product} by name contains search string
     *
     * @param searchString - {@link String} string to find in product name
     * @return - {@link ResponseEntity<List<Product>>} response entity with List of {@link Product} or
     * if there are no such products returns notFound
     */
    @GetMapping("/searchByName/{searchString}")
    public ResponseEntity<List<Product>> findProductsByName(@PathVariable String searchString) {
        try {
            List<Product> productsByNameContains = productService.findProductsByNameContains(searchString);
            return ResponseEntity.ok(productsByNameContains);
        } catch (ProductsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/searchByDescription/{searchString}")
    public ResponseEntity<List<Product>> findProductsByDescription(@PathVariable String searchString) {
        try {
            List<Product> productsByDescriptionContains = productService.findProductsByDescriptionContains(searchString);
            return ResponseEntity.ok(productsByDescriptionContains);
        } catch (ProductsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
