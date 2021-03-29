package com.jm.online_store.service.impl;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.AnswerNotifications;
import com.jm.online_store.repository.AnswerNotificationsRepository;
import com.jm.online_store.repository.AnswerNotificationsRepository;
import com.jm.online_store.service.interf.AnswerNotificationsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnswerNotificationsServiceImpl implements AnswerNotificationsService {

    private final AnswerNotificationsRepository answerNotificationsRepository;

    @Override
    @Transactional
    public void addNotification(Comment comment) {
        AnswerNotifications answerNotifications = new AnswerNotifications();
        answerNotifications.setReviewDate(comment.getCommentDate());
        answerNotifications.setContent(comment.getContent());
        answerNotifications.setProductId(comment.getProductId());
        if (comment.getParentComment() != null){
            answerNotifications.setIdUserComment(comment.getParentComment().getCustomer().getId());
        } else {
            answerNotifications.setIdUserComment(comment.getReview().getCustomer().getId());
        }
        answerNotificationsRepository.save(answerNotifications);
    }

}
