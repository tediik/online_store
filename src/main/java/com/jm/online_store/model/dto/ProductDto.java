package com.jm.online_store.model.dto;

import com.jm.online_store.model.Description;
import com.jm.online_store.model.Product;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи на страницу товара
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для передачи на страницу товара")
public class ProductDto {
    private long id;
    private String product;
    private Double price;
    private Double rating;
    private Description description;
    private String productType;
    private String productPictureName;
    private boolean isFavourite;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.product = product.getProduct();
        this.price = product.getPrice();
        this.rating = product.getRating();
        this.description = product.getDescriptions();
        this.productType = product.getProductType();
        this.productPictureName = product.getProductPictureName();
    }

}
