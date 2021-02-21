package com.jm.online_store.model.dto;

import com.jm.online_store.model.Stock;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для SharedStock")
public class SharedStockDto {
    private Long id;
    private String socialNetworkName;
    private StockDto stockDto;
}
