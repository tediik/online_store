package com.jm.online_store.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * модель DTO для создания ProductCharacteristic по id характеристики
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCharacteristicDto {

    Long characteristicId;
    String value;

}
