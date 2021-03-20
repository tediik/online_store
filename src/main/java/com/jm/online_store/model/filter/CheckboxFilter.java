package com.jm.online_store.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class CheckboxFilter extends FilterData {

    Set<String> label;

    @Override
    public String toString() {
        String checkboxToString = "CheckboxFilter{";
        for (String l : label) {
            checkboxToString += "\nlabel = " + l;
        }
        return checkboxToString + "\n}";
    }
}

