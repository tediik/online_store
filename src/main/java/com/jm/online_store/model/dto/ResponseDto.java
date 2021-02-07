package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto для построения ответа REST контроллеров
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description =  "DTO для построения ответов REST контроллеров")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDto<T> {
    private Boolean success;
    private T data;
    private String error;

    public ResponseDto(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ResponseDto(Boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}