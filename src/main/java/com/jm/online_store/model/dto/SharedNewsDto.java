package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для новостей")
public class SharedNewsDto {
    private Long id;
    private String socialNetworkName;
    private CustomerDto customer;
    private NewsDto news;
}
