package com.jm.online_store.service.spec;

import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.NewsFilterDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@UtilityClass
public class NewsSpec {
    public Specification<News> get(NewsFilterDto filterDto) {
        if (filterDto == null || filterDto.getType() == null) {
            return null;
        }
        LocalDate currentDate = filterDto.getCurrentDate() == null ? LocalDate.now() : filterDto.getCurrentDate();
        switch (filterDto.getType()) {
            case PUBLISHED:
                return Specification.<News>where((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThan(root.get("postingDate"), currentDate))
                        .and((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get("archived"), false));
            case UNPUBLISHED:
                return Specification.<News>where((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThan(root.get("postingDate"), currentDate))
                        .and((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get("archived"), false));
            case ARCHIVED:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("archived"), true);
            default:
                return null;
        }
    }
}
