package com.jm.online_store.model.dto;

import com.jm.online_store.model.SharedNews;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Data
@Slf4j
@ApiModel(description = "DTO для сущности комментария News")
@Component
public class NewsDto {

    private Long id;
    private String title;
    private String anons;
    private String fullText;
    private LocalDate postingDate;
    private LocalDate modifiedDate;
    private boolean archived;
    private Set<SharedNews> sharedNews;
}
