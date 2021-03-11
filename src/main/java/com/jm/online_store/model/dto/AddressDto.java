package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO для сущности адресов Address")
public class AddressDto {
    private Long id;
    private String zip;
    private String region;
    private String district;
    private String city;
    private String street;
    private String building;
}