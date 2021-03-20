package com.jm.online_store.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RangeFilter extends FilterData {

    long min;
    long max;

    @Override
    public String toString() {
        return "RangeFilter{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
