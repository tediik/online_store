package com.jm.online_store.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Topic topic;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "message")
    @Type(type = "text")
    private String message;

    @Column(name = "answer")
    @Type(type = "text")
    private String answer;

    private LocalDateTime responseExpected;

    private LocalDateTime feedbackPostDate;

    public enum Status {
        IN_PROGRESS,
        LATER,
        RESOLVED
    }
}