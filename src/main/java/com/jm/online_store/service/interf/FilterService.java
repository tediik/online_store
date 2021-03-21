package com.jm.online_store.service.interf;

import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.filter.Filters;

import java.util.List;

public interface FilterService {
    Filters getFilters(String category);

    List<ProductDto> filterProducts(String category,
                                   List<Long> price,
                                   List<String> brands,
                                   List<String> color,
                                   List<String> RAM,
                                   List<String> storage,
                                   List<String> screenResolution,
                                   List<String> OS,
                                   List<String> bluetooth);

}
