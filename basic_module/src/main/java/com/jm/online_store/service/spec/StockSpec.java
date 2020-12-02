package com.jm.online_store.service.spec;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockFilterDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 Фильтр для отображения акций по соответствующим закладкам
 */
@UtilityClass
public class StockSpec {
    public Specification<Stock> get(StockFilterDto filterDto) {
        if (filterDto == null || filterDto.getType() == null) {
            return null;
        }
        LocalDate currentDate = filterDto.getCurrentDate() == null ? LocalDate.now() : filterDto.getCurrentDate();
        switch (filterDto.getType()) {
            case CURRENT:
                return Specification.<Stock>where((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), currentDate))
                        .and((root, query, criteriaBuilder) ->
                                criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), currentDate));
            case FUTURE:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThan(root.get("startDate"), currentDate);
            case PAST:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThan(root.get("endDate"), currentDate);
            default:
                return null;
        }
    }
}
