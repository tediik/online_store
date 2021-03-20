package com.jm.online_store.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Filter {

    String filterType;
    String label;
    String key;
    FilterData filterData;

    @Override
    public String toString() {
        return "Filter{" +
                "filterType=" + filterType +
                ", label='" + label + '\'' +
                ", key='" + key + '\'' +
                ", filterData=" + filterData +
                '}';
    }
}

