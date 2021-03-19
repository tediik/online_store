package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description =  "Dto для Feedback - обратная связь, связана с User")
public class FeedbackDto {

    private Long id;

    private Topic topic;

    private User user;

    private Feedback.Status status;

    private String message;

    private String answer;

    private Long managerId;

    private LocalDateTime responseExpected;

    private LocalDateTime feedbackPostDate;

    public enum Status {
        IN_PROGRESS,
        LATER,
        RESOLVED
    }
}
