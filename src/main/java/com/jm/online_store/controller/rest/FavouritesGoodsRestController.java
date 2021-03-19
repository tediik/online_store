package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.Response;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.FavouritesGroupDto;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.FavouriteGoodsService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Рест контроллер для избранных товаров.
 */
@AllArgsConstructor
@RestController
@Api(value = "Rest Controller for favourite products")
@RequestMapping("/api/customer")
public class FavouritesGoodsRestController {
    private final FavouriteGoodsService favouriteGoodsService;
    private final UserService userService;
    private final FavouritesGroupService favouritesGroupService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<ProductDto>>() {}.getType();

    /**
     * контроллер для получения товаров "избранное" для авторизованного User.
     * используется поиск по идентификатору User, т.к. используется ленивая
     * загрузка товаров, добавленных в "избранное".
     *
     * @return ResponseEntity<> список избранных товаров данного User + статус ответа.
     */
    @GetMapping(value = "/favouritesGoods")
    @ApiOperation(value = "Rest Controller fetches products from Favourite products for current logged in User",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<ProductDto>>> getFavouritesGoods() {
        User user = userService.getCurrentLoggedInUser();
        List<ProductDto> productDtoSet = modelMapper.map(favouriteGoodsService.getFavouriteGoods(user), listType);
        return new ResponseEntity<>(new ResponseDto<>(true, productDtoSet), HttpStatus.OK);
    }

    /**
     * Контроллер добавления товара в избранное.
     * Добавляем продукт в список избранного "Все товары"
     * @param id идентификатор товара
     * @return ResponseEntity.ok()
     */
    @PutMapping(value = "/favouritesGoods")
    @ApiOperation(value = "Rest Controller adds products to favourites. Adds it to the list \"All products\" ",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<FavouritesGroupDto>> addFavouritesGoods(@RequestBody Long id) {
        Customer customer = customerService.getCurrentLoggedInCustomer();
        favouriteGoodsService.addToFavouriteGoods(id, customer);
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        FavouritesGroup favouritesGroup = favouritesGroupService.getOneFavouritesGroupByUserAndByName(customer, "Все товары");
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(favouritesGroupService.addProductToFavouritesGroup(product, favouritesGroup), FavouritesGroupDto.class)), HttpStatus.OK);
    }

    /**
     * Контроллер удаления товара из избранного списка товаров.
     * Удаляем продукт из списка "Все товары"
     * @param id идентификатор товара
     * @return ResponseEntity.ok()
     */
    @DeleteMapping(value = "/favouritesGoods")
    @ApiOperation(value = "Rest Controller deletes product from favourites. From the list \"All products\" ",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> deleteFromFavouritesGoods(@RequestBody Long id) {
        Customer customer = customerService.getCurrentLoggedInCustomer();
        favouriteGoodsService.deleteFromFavouriteGoods(id, customer);
        Product product = productService.findProductById(id).orElseThrow(ProductNotFoundException::new);
        FavouritesGroup favouritesGroup = favouritesGroupService.getOneFavouritesGroupByUserAndByName(customer, "Все товары");
        favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product, favouritesGroup);
        return new ResponseEntity<>(new ResponseDto<>(true, "Successful deleted", Response.NO_ERROR.getText()), HttpStatus.OK);
    }

    @ExceptionHandler({UserNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<ResponseDto<?>> handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}
