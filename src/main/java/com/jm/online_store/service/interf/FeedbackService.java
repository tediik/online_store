package com.jm.online_store.service.interf;

import com.jm.online_store.model.Feedback;

import java.util.List;

public interface FeedbackService {
    void addFeedbackFromDto(Feedback feedback);
    void deleteFeedbackById(Long id);
    List<Feedback> getAllFeedback();
    List<Feedback> getInProgressFeedback();
    List<Feedback> getLaterFeedback();
    List<Feedback> getResolvedFeedback();
}