package com.jm.online_store.service.interf;

import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    List<Review> findAll(Long productId);

    List<Review> findAllByCustomer(User user);

    Review addReview(@RequestBody Review review);

    void addReviewInit(Review review);

    Optional<Review> findById(Long parentId);
}
