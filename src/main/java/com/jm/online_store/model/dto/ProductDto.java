package com.jm.online_store.model.dto;

import com.jm.online_store.model.Description;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для передачи на страницу товара
 */
@Data
@AllArgsConstructor
@ApiModel(description =  "DTO для передачи на страницу товара")
public class ProductDto {
    private long id;
    private String product;
    private Double price;
    private Double rating;
    private Description descriptions;
    private String productType;
    private String ProductPictureName;
    private boolean isFavourite;
}
