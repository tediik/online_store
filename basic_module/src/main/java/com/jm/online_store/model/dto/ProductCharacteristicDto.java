package com.jm.online_store.model.dto;

import com.jm.online_store.model.Characteristic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCharacteristicDto {

    Long characteristicId;
    String value;
}
