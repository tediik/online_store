package com.jm.online_store.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum FilterType {
    RANGE("range"),
    CHECKBOX("checkbox");

    private String message;
}
