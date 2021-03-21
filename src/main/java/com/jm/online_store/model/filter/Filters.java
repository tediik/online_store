package com.jm.online_store.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * сущность для формирования списка фильтров в один объект
 * (по просьбе фронтов)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Filters {
    private List<Filter> filters;
}
