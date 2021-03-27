package com.jm.online_store.service.impl;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.ReviewNotifications;
import com.jm.online_store.repository.ReviewNotificationsRepository;
import com.jm.online_store.service.interf.ReviewNotificationsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReviewNotificationsServiceImpl implements ReviewNotificationsService {

    private final ReviewNotificationsRepository reviewNotificationsRepository;

    @Override
    @Transactional
    public void addReview(Comment comment) {
        ReviewNotifications reviewNotifications = new ReviewNotifications();
        reviewNotifications.setReviewDate(comment.getCommentDate());
        reviewNotifications.setContent(comment.getContent());
        reviewNotifications.setProductId(comment.getProductId());
        reviewNotifications.setIdUserComment(comment.getReview().getCustomer().getId());

        reviewNotificationsRepository.save(reviewNotifications);
    }
}
