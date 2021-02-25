package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Api("Rest controller for search")
@RequestMapping("api/search/")
public class SearchRestController {

    /**
     * Метод для получения строки, которая используется для поиска товаров
     *
     * @param searchString Строка поиска
     * @return ResponseEntity(searchString) возвращает строку поиска
     */
    @GetMapping("/{searchString}")
    @ApiOperation(value = "Get searchString",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "Product was not found"),
            @ApiResponse(code = 200, message = "Product found"),
    })
    public ResponseEntity<String> searchProductByString(@PathVariable String searchString) {
        return ResponseEntity.ok(searchString);
    }
}

