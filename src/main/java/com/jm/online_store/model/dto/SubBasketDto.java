package com.jm.online_store.model.dto;

import com.jm.online_store.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubBasketDto {
    private Long id;
    private Product product;
    private int count;
}
