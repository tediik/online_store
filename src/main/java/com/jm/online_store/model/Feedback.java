package com.jm.online_store.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "topic")
    @OneToOne
    private FeedbackTopic topic;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Column(name = "feedback_post_date")
    private LocalDateTime feedbackPostDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private String status;

    public enum Status {
        TO_DO,
        IN_PROGRESS,
        RESOLVED
    }
}
