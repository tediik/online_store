package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(description =  "DTO для данных продукта")
public class ProductModelDto {
    private long id;
    private String product;
    private Double price;
    private Integer amount;
    private Double rating;
    private String productType;
    private boolean deleted;
    private String productPictureName;
}

