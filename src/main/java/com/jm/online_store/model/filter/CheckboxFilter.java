package com.jm.online_store.model.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * сущность для checkbox фильтра
 * label - список объектов с данными для фильтров (требование фронтов)
 */
@Getter
@Setter
@AllArgsConstructor
public class CheckboxFilter implements FilterData {

    @JsonProperty("data")
    private Set<Label> labels;

    @Override
    public String toString() {
        return "CheckboxFilter{" +
                "labels=" + labels +
                '}';
    }

    /**
     * сущность для формирования списка фильтров
     * Comparable - для сортировки по имени
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Label implements Comparable<Label> {
        private String label;

        @Override
        public String toString() {
            return "Label {" +
                    "label = " + label +
                    '}';
        }

        @Override
        public int compareTo(Label o) {
            return label.compareTo(o.getLabel());
        }
    }
}

