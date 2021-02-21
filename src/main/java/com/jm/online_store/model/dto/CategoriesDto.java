package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@ApiModel(description = "DTO для сущности комментария Categories")
@Component
public class CategoriesDto {
    private Long id;
    private String category;
    private Long parentCategoryId;
    private int depth;
}
