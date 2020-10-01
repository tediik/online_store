package com.jm.online_store.model.dto;

import com.jm.online_store.model.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * DTO для передачи на страницу товара
 */
@Data
@AllArgsConstructor
public class ProductDto {
    private long id;
    private String product;
    private Double price;
    private Double rating;
    private Description descriptions;
    private String productType;
    private boolean isFavourite;
}
