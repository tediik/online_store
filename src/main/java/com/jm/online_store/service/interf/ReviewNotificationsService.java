package com.jm.online_store.service.interf;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Review;
import org.springframework.web.bind.annotation.RequestBody;

public interface ReviewNotificationsService {
    void addReview(Comment comment);
}
