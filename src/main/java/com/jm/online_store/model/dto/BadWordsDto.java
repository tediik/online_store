package com.jm.online_store.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Dto для BadWords")
public class BadWordsDto {
    private Long id;
    private String badword;
    private boolean isEnabled;
}
