package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
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
public class CustomerTrackableProductRestController {
    private final ProductService productService;

    /**
     * Метод для получения всех товаров, на изменение цены которых подписан
     * залогиненный пользователь
     *
     * @return ResponseEntity<List < Product>> возвращает товары со статусом ответа,
     * если товаров нет - только статус
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllTrackableProducts() {
        List<Product> trackableProducts = productService.findAllTrackableProductsByCurrentLoggedInUser();
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
    public ResponseEntity<Long> deleteProducts(@PathVariable(name = "id") long id) {
        productService.deleteProductFromTrackedForCurrentLoggedInUser(id);
        return ResponseEntity.ok(id);
    }
}
