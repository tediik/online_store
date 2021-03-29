package com.jm.online_store.service.interf;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Review;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

public interface AnswerNotificationsService {
    void addNotification(Comment comment);
}
