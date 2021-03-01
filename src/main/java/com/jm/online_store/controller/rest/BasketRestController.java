package com.jm.online_store.controller.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.ProductsNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Рест контроллер для корзины.
 */
@AllArgsConstructor
@RestController
@Api(description = "Rest controller for actions with Basket and SubBasket")
@RequestMapping("/api")
public class BasketRestController {
    private final BasketService basketService;
    private final UserService userService;

    /**
     * контроллер для получения товаров в корзине для авторизованного User.
     *
     * @return ResponseEntity<> список товаров данного User + статус ответа.
     */
    @GetMapping(value = "/basketGoods")
    @ApiOperation(value = "get all items for authorised User",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<SubBasket>>> getBasket(HttpServletRequest request) {
        List<SubBasket> subBaskets = basketService.getBasket(request.getSession().getId());
        return new ResponseEntity<>(new ResponseDto<>(true, subBaskets), HttpStatus.OK);
        //return new ResponseEntity<>(subBaskets, HttpStatus.OK);
    }

    /**
     * контроллер для формирования заказа из корзины.
     *
     * @param id адрес с формы
     * @return ResponseEntity.ok()
     */
    @PostMapping(value = "/basketGoods")
    @ApiOperation(value = "builds new order from basket",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<Order>> buildOrderFromBasket(@RequestBody Long id) {
        Order order = basketService.buildOrderFromBasket(id);
        return new ResponseEntity<>(new ResponseDto<>(true, order),HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }


    /**
     * контроллер для удаления сущности SubBasket из списка подкорзин User.
     *
     * @param id идентификатор подкорзины
     * @return ResponseEntity.ok()
     */
    @DeleteMapping(value = "/basketGoods")
    @ApiOperation(value = "Deletes entity SubBasket from the SubBaskets list by id",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> deleteBasket(@RequestBody Long id, HttpServletRequest request) {
        basketService.deleteBasket(basketService.findBasketById(id), request.getSession().getId());
        return new ResponseEntity<>(new ResponseDto<>(true, "SubBasket successful deleted"), HttpStatus.OK);
        //return ResponseEntity.ok().build();
    }

    /**
     * контроллер для обновления количества товара в подкорзине.
     *
     * @param json json из 2-х параметров
     * @return ResponseEntity.ok()
     */
    @PutMapping(value = "/basketGoods")
    @ApiOperation(value = "Updates the items quantity in SubBasket",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<ObjectNode>> updateUpBasket(@RequestBody ObjectNode json) {
        Long id = json.get("id").asLong();
        int difference = json.get("count").asInt();
        try {
            basketService.updateBasket(basketService.findBasketById(id), difference);
            return new ResponseEntity<>(new ResponseDto<>(true, json), HttpStatus.OK);
            //return ResponseEntity.ok().build();
        } catch (ProductsNotFoundException e) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Products not found"), HttpStatus.BAD_REQUEST);
            //return ResponseEntity.badRequest().build();
        }

    }

    @PutMapping("/basket/add/{id}")
    @ApiOperation(value = "Adds product to Basket",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<SubBasket>> addProductToBasket(@PathVariable Long id, HttpServletRequest request) {
        try {
            SubBasket subBasket = basketService.addProductToBasket(id, request.getSession().getId());
            return new ResponseEntity<>(new ResponseDto<>(true, subBasket), HttpStatus.OK);
            //return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ResponseDto<>(false, "User not found"), HttpStatus.NOT_FOUND);
            //return ResponseEntity.notFound().build();
        } catch (ProductsNotFoundException e) {
            return new ResponseEntity<>(new ResponseDto<>(true, "Products not found"), HttpStatus.BAD_REQUEST);
            //return ResponseEntity.badRequest().build();
        }
    }
}
