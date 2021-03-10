package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description =  "DTO для SharedStock")
public class SharedStockDto {
    private Long id;
    private String socialNetworkName;
    private StockDto stockDto;
}
