package com.jm.online_store.service.impl;

import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.Comment;
import com.jm.online_store.model.PriceChangeNotifications;
import com.jm.online_store.model.Review;
import com.jm.online_store.model.AnswerNotifications;
import com.jm.online_store.repository.AnswerNotificationsRepository;
import com.jm.online_store.repository.AnswerNotificationsRepository;
import com.jm.online_store.service.interf.AnswerNotificationsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AnswerNotificationsServiceImpl implements AnswerNotificationsService {

    private final AnswerNotificationsRepository answerNotificationsRepository;

    /**
     * Добавление в базу данных уведомлений об ответах на комментарии и отзывы
     * @param comment {@link Comment} - комментарий
     */
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

    /**
     * Поиск уведомлений по id покупателя
     * @param id {@link Long} - id покупателя
     * @return List<AnswerNotifications> - список уведомлений для покупателя
     */
    @Override
    @Transactional
    public List<AnswerNotifications> getCustomerAnswerNotifications(Long id) {
        return answerNotificationsRepository.findByIdUserComment(id);
    }

    /**
     * Удаление уведомлений по id
     * @param id {@link Long} - id уведомления
     *
     */
    @Override
    @Transactional
    public void deleteNotification (Long id) {
        answerNotificationsRepository.deleteById(id);
    }

}
