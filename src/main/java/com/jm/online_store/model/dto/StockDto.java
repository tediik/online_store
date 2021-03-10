package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для Stock")
public class StockDto {
    private Long id;
    private String stockImg;
    private String stockTitle;
    private String stockText;
    private LocalDate startDate;
    private LocalDate endDate;
}
