package com.jm.online_store.service.interf;

import com.jm.online_store.model.filter.Filters;

public interface FilterService {
    Filters getSmartphoneFilters (String category);
}
