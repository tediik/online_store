package com.jm.online_store.service.interf;

import com.jm.online_store.model.Feedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface FeedbackService {

    void addFeedbackFromDto(Feedback feedback);

    void addAnswerFeedback(Feedback feedback);

    void updateTimeAnswerFeedback(Feedback feedback);

    void returnInWork(Long id);

    void deleteFeedbackById(Long id);

    Set<Feedback> getAllFeedbackCurrentCustomer();

    List<Feedback> getAllFeedback();

    List<Feedback> getInProgressFeedback();

    List<Feedback> getLaterFeedback();

    List<Feedback> getResolvedFeedback();

    Feedback getFeedbackById(Long id);

    LocalDateTime getDateTimeFeedback(Long id);
}