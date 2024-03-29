package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jm.online_store.model.News;
import com.jm.online_store.model.User;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedNewsDto {
    private Long id;
    private String socialNetworkName;
    private User user;
    private News news;
}
