package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description =  "Сущность Feedback - обратная связь, связана с User")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Topic topic;

    @ManyToOne
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "message")
    @Type(type = "text")
    private String message;

    @Column(name = "answer")
    @Type(type = "text")
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
