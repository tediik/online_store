package com.jm.online_store.model.dto;

import com.jm.online_store.enums.BadWordStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadWordsDto {
    private Long id;
    private String badword;
    private BadWordStatus status;
}
