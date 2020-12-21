package com.jm.online_store.model.dto;

import com.jm.online_store.enums.NewsFilterType;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description =  "DTO для новостного фильтра")
public class NewsFilterDto {
    NewsFilterType type;
    LocalDate currentDate;
}
