package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Set;

/**
 * Рест контроллер по работе с продуктами в списках избранного
 */
@RestController
@AllArgsConstructor
@Api(description = "Rest controller for products in favorite group")
@RequestMapping("/api/customer")
public class ProductForFavouritesGroupRestController {

    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;



    /**
     * Добавление продукта в Список избранных товаров
     * @param product продукт
     * @param id идентификатор списка избранных товаров
     * @return ResponseEntity<ResponseDto<FavouritesGroup>>
     */
    @PostMapping(value = "/addProductInFavouritesGroup/{id}")
    @ApiOperation(value = "Add product in favourite group by group ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Product has been added"),
            @ApiResponse(code = 400, message = "Product hasn't been added")
    })
    public ResponseEntity <ResponseDto<FavouritesGroup>> addProductInFavouritesGroup(@RequestBody Product product,
                                                                            @PathVariable("id") Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroupService.addProductToFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok(new ResponseDto<>(true, favouritesGroup));
    }

    /**
     * Получение продуктов из списка избранных товаров
     * @param id идентификатор списка избранных товаров
     * @return
     */
    @GetMapping(value = "/getProductFromFavouritesGroup/{id}")
    @ApiOperation(value = "Get product from favourite group by group ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products have been found"),
            @ApiResponse(code = 200, message = "Products have not been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<Set<Product>>> getProductFromFavouritesGroup(@PathVariable Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        return ResponseEntity.ok(new ResponseDto<>(true, favouritesGroupService.getProductSet(favouritesGroup)));
    }




    /**
     * Удаление продукта из выбранного списка избранного
     * @param idProduct идентификатор продукта
     * @param id идентификатор списка
     * @return
     */
    @DeleteMapping(value = "/deleteProductFromFavouritesGroup/{idGroup}")
    @ApiOperation(value = "Delete product from favourite group by group ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product has been deleted"),
            @ApiResponse(code = 404, message = "Product hasn't been found"),
    })
    public ResponseEntity <ResponseDto<Long>> deleteProductFromFavouritesGroup(@RequestBody Long idProduct, @PathVariable("idGroup") Long id) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        Product product = productService.findProductById(idProduct).orElseThrow();
        favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, favouritesGroup);
        return ResponseEntity.ok(new ResponseDto<>(true, id));
    }

    /**
     * Удаление продукта из выбранного списка избранного
     * @param idProducts Масссив id продуктов
     * @param idGroup идентификатор списка
     * @return
     */
    @DeleteMapping(value = "/clearFavouritesGroup/{idGroup}")
    @ApiOperation(value = "Delete all products from favourite group by group ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products have been deleted"),
            @ApiResponse(code = 404, message = "Products havn't been found"),
    })

    public ResponseEntity <ResponseDto<Long>> deleteFromFavouritesGroupProductAll(@RequestBody ArrayList<Long> idProducts, @PathVariable("idGroup") Long idGroup) {
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(idGroup).orElseThrow();
        for (int i = 0; i < idProducts.size(); i++){
            Product product = productService.findProductById(idProducts.get(i)).orElseThrow();
            favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, favouritesGroup);
        }
        return ResponseEntity.ok(new ResponseDto<>(true, idGroup));
    }

    /**
     * Перемещение продуктов из одного списка в другой
     * @param idProducts  Масссив id продуктов
     * @param idNewGroup идентификатор нового списка
     * @param idOldGroup идентификатор старого списка
     * @return
     */
    @PutMapping(value = "/deleteProductFromFavouritesGroup/{idNewGroup}/{idOldGroup}")
    @ApiOperation(value = "Remove products from favorite group to another favorite group by old group ID and new group ID")
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "Products have been successfully removed"),
            @ApiResponse(code = 400, message = "Products havn't been removed, smth goes wrong...")
    })
    public ResponseEntity<ResponseDto<String>> moveProducts(@RequestBody ArrayList<Long> idProducts, @PathVariable("idNewGroup") Long idNewGroup, @PathVariable("idOldGroup") Long idOldGroup) {
        FavouritesGroup newFavouritesGroup = favouritesGroupService.findById(idNewGroup).orElseThrow();
        FavouritesGroup oldFavouritesGroup = favouritesGroupService.findById(idOldGroup).orElseThrow();
        for (int i = 0; i < idProducts.size(); i++) {
            Product product = productService.findProductById(idProducts.get(i)).orElseThrow();
            favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, oldFavouritesGroup);
            favouritesGroupService.addProductToFavouritesGroup(product, newFavouritesGroup);
        }
        return ResponseEntity.ok( new ResponseDto<>(true, ResponseOperation.SUCCESS.getMessage()));
    }
}

