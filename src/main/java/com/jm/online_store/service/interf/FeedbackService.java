package com.jm.online_store.service.interf;

import com.jm.online_store.model.Feedback;
import com.jm.online_store.model.dto.FeedbackRequestDto;

public interface FeedbackService {
    void addFeedbackFromDto(Feedback feedback);
}
