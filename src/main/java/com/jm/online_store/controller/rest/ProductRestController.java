package com.jm.online_store.controller.rest;

import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * рест контроллер для Product.
 */
@RestController
@AllArgsConstructor
public class ProductRestController {
   private final ProductService productService;

    /**
     *контроллер для получения Map содержащим ключ-значение: дата.время изменения цены - цена.
     *
     * @param id идентификатор продукта
     * @return map содержащая значения по изменению цены на товар.
     */
    @PostMapping("manager/productChangeMonitor")
    public ResponseEntity<Map> priceMonitor(@RequestBody Long id) {
        return ResponseEntity.ok(productService.getProductPriceChange(id));
    }

}
