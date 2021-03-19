package com.jm.online_store.service.interf;

import com.jm.online_store.model.Feedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface FeedbackService {

    Feedback addFeedbackFromDto(Feedback feedback);

    Feedback addAnswerFeedback(Feedback feedback);

    Feedback updateTimeAnswerFeedback(Feedback feedback);

    Feedback returnInWork(Long id);

    void deleteFeedbackById(Long id);

    Set<Feedback> getAllFeedbackCurrentCustomer();

    List<Feedback> getAllFeedback();

    List<Feedback> getInProgressFeedback();

    List<Feedback> getLaterFeedback();

    List<Feedback> getResolvedFeedback();

    Feedback getFeedbackById(Long id);

    LocalDateTime getDateTimeFeedback(Long id);
}