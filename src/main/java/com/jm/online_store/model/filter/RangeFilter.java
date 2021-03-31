package com.jm.online_store.model.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * сущность для range фильтра (от ... до ...)
 */
@Getter
@Setter
@AllArgsConstructor
public class RangeFilter implements FilterData {

    @JsonIgnore
    long min;

    @JsonIgnore
    long max;

    @JsonProperty(value = "data")
    public Map<String, Long> getData() {
        Map<String, Long> map = new HashMap<>();
        map.put("min", getMin());
        map.put("max", getMax());
        return map;
    }

    @Override
    public String toString() {
        return "RangeFilter{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
