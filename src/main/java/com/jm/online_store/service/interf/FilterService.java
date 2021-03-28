package com.jm.online_store.service.interf;

import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.filter.Filters;

import java.util.List;
import java.util.Map;

public interface FilterService {
    Filters getFilters(String category);

    List<ProductDto> getProductsByFilter(String category, Map<String, String> labels);
}
