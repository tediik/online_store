package com.jm.online_store.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для передачи со страницы добавления характеристик категории
 */
@Data
@AllArgsConstructor
public class CharacteristicDto {
    private String characteristicName;
}
