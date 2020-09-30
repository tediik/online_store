package com.jm.online_store.service.impl;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.repository.FeedbackRepository;
import com.jm.online_store.service.interf.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    @Override
    public void addFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }
}
