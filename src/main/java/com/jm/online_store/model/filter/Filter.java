package com.jm.online_store.model.filter;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Filter {

    FilterType type;
    String label;
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

