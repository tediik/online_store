package com.jm.online_store.model.dto;

import com.jm.online_store.model.Feedback;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@ApiModel(description = "DTO для сущности комментария FeedBack")
@Component
public class FeedBackDto {
    private Long id;
    private Feedback.Status status;
    private String message;
    private String answer;
    private Long managerId;
}
