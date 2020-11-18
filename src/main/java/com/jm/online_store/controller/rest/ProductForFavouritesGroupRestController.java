package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.FavouritesGroupProductService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Set;

/**
 * Рест контроллер по работе с продуктами в списках избранного
 */
@RestController
@AllArgsConstructor
public class ProductForFavouritesGroupRestController {

    private final FavouritesGroupProductService favouritesGroupProductService;
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;

    /**
     * Добавление продукта в Список избранных товаров
     * @param product продукт
     * @param id идентификатор списка избранных товаров
     * @return
     */
    @PostMapping(value = "/customer/addProductInFavouritesGroup/{id}")
    public ResponseEntity addProductInFavouritesGroup(@RequestBody Product product, @PathVariable("id") Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroupProductService.addProductToFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok("addProductInFavouritesGroupOK");
    }

    /**
     * Получение продуктов из списка избранных товаров
     * @param id идентификатор списка избранных товаров
     * @return
     */
    @GetMapping(value = "/customer/getProductFromFavouritesGroup/{id}")
    public ResponseEntity<Set<Product>> getProductFromFavouritesGroup(@PathVariable Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        return ResponseEntity.ok(favouritesGroupProductService.getProductSet(favouritesGroup));
    }

    /**
     * Удаление продукта из выбранного списка избранного
     * @param idProduct идентификатор продукта
     * @param id идентификатор списка
     * @return
     */
    @DeleteMapping(value = "/customer/deleteProductFromFavouritesGroup/{idGroup}")
    public ResponseEntity deleteProductFromFavouritesGroup(@RequestBody Long idProduct, @PathVariable("idGroup") Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        Product product = productService.findProductById(idProduct).orElseThrow();
        favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление продукта из выбранного списка избранного
     * @param idProducts Масссив id продуктов
     * @param idGroup идентификатор списка
     * @return
     */
    @DeleteMapping(value = "/customer/clearFavouritesGroup/{idGroup}")
    public ResponseEntity deleteFromFavouritesGroupProductAll(@RequestBody ArrayList<Long> idProducts, @PathVariable("idGroup") Long idGroup) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(idGroup).orElseThrow();
        for (int i = 0; i < idProducts.size(); i++){
            Product product = productService.findProductById(idProducts.get(i)).orElseThrow();
            favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, favouritesGroup);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Перемещение продуктов из одного списка в другой
     * @param idProducts  Масссив id продуктов
     * @param idNewGroup идентификатор нового списка
     * @param idOldGroup идентификатор старого списка
     * @return
     */
    @PutMapping(value = "/customer/deleteProductFromFavouritesGroup/{idNewGroup}/{idOldGroup}")
    public ResponseEntity moveProducts(@RequestBody ArrayList<Long> idProducts, @PathVariable("idNewGroup") Long idNewGroup, @PathVariable("idOldGroup") Long idOldGroup) {
        FavouritesGroup newFavouritesGroup = favouritesGroupService.findById(idNewGroup).orElseThrow();
        FavouritesGroup oldFavouritesGroup = favouritesGroupService.findById(idOldGroup).orElseThrow();
        for (int i = 0; i < idProducts.size(); i++) {
            Product product = productService.findProductById(idProducts.get(i)).orElseThrow();
            favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, oldFavouritesGroup);
            favouritesGroupProductService.addProductToFavouritesGroup(product, newFavouritesGroup);
        }
        return ResponseEntity.ok().build();
    }
}

