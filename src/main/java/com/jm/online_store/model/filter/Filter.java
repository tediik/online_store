package com.jm.online_store.model.filter;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Сущность для осуществления фильтрации и формирования фильтров
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Filter {

    FilterType type;
    //подпись формы на странице
    String label;
    //ключ, по которому возвращаются данные для фильтрации
    String key;

    @JsonUnwrapped
    FilterData data;

    @Override
    public String toString() {
        return "Filter{" +
                "filterType=" + type +
                ", label='" + label + '\'' +
                ", key='" + key + '\'' +
                ", filterData=" + data +
                '}';
    }
}

