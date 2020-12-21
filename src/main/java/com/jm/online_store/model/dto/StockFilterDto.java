package com.jm.online_store.model.dto;

import com.jm.online_store.enums.StockFilterType;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description =  "DTO для фильтра акций")
public class StockFilterDto {
    StockFilterType type;
    LocalDate currentDate;
}
