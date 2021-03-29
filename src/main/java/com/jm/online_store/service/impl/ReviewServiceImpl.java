package com.jm.online_store.service.impl;

import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ReviewRepository;
import com.jm.online_store.service.interf.AnswerNotificationsService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    /**
     * Получает arrayList все отзывы по productId
     *
     * @param productId
     * @return List<review>
     */
    @Override
    public List<Review> findAll(Long productId) {
        return reviewRepository.findAllByProductId(productId);
    }

    /**
     * Найти и вернуть список отзывов из базы данных по идентификатору клиента
     *
     * @return List<review>
     */
    @Override
    public List<Review> findAllByCustomer(User user) {
        return reviewRepository.findReviewsByCustomer(user);
    }

    /**
     * Метод проверяет, является ли комментарий новым сообщением
     * или ответом на предыдущий отзыв затем устанавливает
     * текущего пользователя как автора отзыва и сохраняет его в базе данных
     *
     * @param review
     * @return Review
     */
    @Override
    @Transactional
    public Review addReview(Review review) {
        User loggedInUser = userService.getCurrentLoggedInUser();
        review.setCustomer(userService.findById(loggedInUser.getId()).get());
        return reviewRepository.save(review);
    }

    /**
     * Найти и получить отзыв о продукте из базы данных по идентификатору отзыва
     *
     * @return ProductReview
     */
    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    /**
     * Инициализация отзывов в DataInitializer
     *
     * @param review
     */
    @Override
    @Transactional
    public void addReviewInit(Review review) {
        reviewRepository.save(review);
    }
}
