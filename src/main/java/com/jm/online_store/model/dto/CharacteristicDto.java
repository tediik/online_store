package com.jm.online_store.model.dto;

import com.jm.online_store.model.ProductCharacteristic;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Slf4j
@ApiModel(description = "DTO для сущности комментария Characteristic")
@Component
public class CharacteristicDto {
    private Long id;
    private String characteristicName;



}
