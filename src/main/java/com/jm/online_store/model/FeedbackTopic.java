package com.jm.online_store.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Data
public class FeedbackTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "topic_category")
    private String topicCategory;
    @Column(name = "topic-name")
    private String topicName;
    @OneToMany(mappedBy = "topic")
    private Set<Feedback> feedbacks;
}
