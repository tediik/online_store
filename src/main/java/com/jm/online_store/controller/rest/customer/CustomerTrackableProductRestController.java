package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RestController для вывода/удаления товаров, за изменением цен которых следит
 * зарегистрированный пользователь
 */
@RestController
@RequestMapping("/api/customer/trackableProduct")
@AllArgsConstructor
@Api(description = "Rest Controller for fetching or deleting products which prices are tracked by User")
public clasCustomerTrackableProductRestController {
    private final ProductService productService;

    /**
     * Метод для получения всех товаров, на изменение цены которых подписан
     * залогиненный пользователь
     *
     * @return ResponseEntity<List < Product>> возвращает товары со статусом ответа,
     * если товаров нет - только статус
     */
    @GetMapping
    @ApiOperation(value = "Gets all the products which price current logged in User is tracking")
    public ResponseEntity<List<Product>> getAllTrackableProducts() {
        List<Product> trackableProducts = productService.findTrackableProductsByLoggedInUser();
        if (trackableProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(trackableProducts);
    }

    /**
     * Метод для удаления подписки на изменение цены конкретного товара
     *
     * @param id идентификатор товара, на который подписан залогиненный пользователь
     * @return ResponseEntity<Long> с идентификатором товара, на изменение цены которого,
     * пользователь уже не подписан со статусом ответа
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes price tracking for current product")
    public ResponseEntity<Long> deleteProducts(@PathVariable(name = "id") long id) {
        productService.deleteProductFromTrackedForLoggedInUser(id);
        return ResponseEntity.ok(id);
    }
}
