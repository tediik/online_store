package com.jm.online_store.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jm.online_store.model.SharedNews;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDate postingDate;
    private boolean archived;

    public NewsDto() {
    }

    public NewsDto(Long id, String title, String anons, String fullText, LocalDate postingDate, boolean archived) {
        this.id = id;
        this.title = title;
        this.anons = anons;
        this.fullText = fullText;
        this.postingDate = postingDate;
        this.archived = archived;
    }
}
