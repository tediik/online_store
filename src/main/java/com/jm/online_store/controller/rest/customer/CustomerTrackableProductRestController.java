package com.jm.online_store.controller.rest.customer;

import com.jm.online_store.model.dto.ProductModelDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RestController для вывода/удаления товаров, за изменением цен которых следит
 * зарегистрированный пользователь
 */
@RestController
@RequestMapping("/api/customer/trackableProduct")
@AllArgsConstructor
@Api(description = "Rest Controller for fetching or deleting products which prices are tracked by User")
public class CustomerTrackableProductRestController {
    private final ProductService productService;
    private final ModelMapper modelMapper;

    /**
     * Метод для получения всех товаров, на изменение цены которых подписан
     * залогиненный пользователь
     *
     * @return ResponseEntity<ResponseDto<List<ProductModelDto>>>  товары со статусом 200,
     * если товаров нет - пустой массив и статус 200
     */
    @GetMapping
    @ApiOperation(value = "Gets all the products which price current logged in User is tracking",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<List<ProductModelDto>>> getAllTrackableProducts() {
        List<ProductModelDto> trackableProducts = productService.findTrackableProductsByLoggedInUser()
                .stream()
                .map(product -> modelMapper.map(product, ProductModelDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ResponseDto<>(true, trackableProducts), HttpStatus.OK);
    }

    /**
     * Метод для удаления подписки на изменение цены конкретного товара
     *
     * @param id идентификатор товара, на который подписан залогиненный пользователь
     * @return ResponseEntity<Long> с идентификатором товара, на изменение цены которого,
     * пользователь уже не подписан со статусом ответа
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes price tracking for current product",
            authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<Long>> deleteProducts(@PathVariable(name = "id") long id) {
        productService.deleteProductFromTrackedForLoggedInUser(id);
        return new ResponseEntity<>(new ResponseDto<>(true, id), HttpStatus.OK);
    }
}
