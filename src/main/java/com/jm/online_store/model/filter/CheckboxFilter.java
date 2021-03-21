package com.jm.online_store.model.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class CheckboxFilter extends FilterData {

    @JsonProperty("data")
    private Set<Label> labels;

    @Override
    public String toString() {
        return "CheckboxFilter{" +
                "labels=" + labels +
                '}';
    }

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

