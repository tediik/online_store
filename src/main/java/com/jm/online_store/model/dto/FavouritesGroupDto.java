package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties("user")
@ApiModel(description =  "Dto для сущности FavouritesGroup - список избранных товаров")
public class FavouritesGroupDto {
    private Long id;
    private String name;
    private User user;
    private Set<Product> products;
}
