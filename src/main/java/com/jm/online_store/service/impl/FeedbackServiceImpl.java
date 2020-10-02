package com.jm.online_store.service.impl;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.repository.FeedbackRepository;
import com.jm.online_store.repository.TopicRepository;
import com.jm.online_store.service.interf.FeedbackService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final TopicRepository topicRepository;
    private final UserService userService;

    @Override
    public void addFeedbackFromDto(Feedback feedback) {
        feedback.setFeedbackPostDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        feedback.setStatus(Feedback.Status.TO_DO);

        feedbackRepository.save(feedback);
    }
}
