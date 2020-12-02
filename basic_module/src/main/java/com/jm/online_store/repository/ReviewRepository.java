package com.jm.online_store.repository;

import com.jm.online_store.model.Review;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByProductId(Long productId);

    List<Review> findReviewsByCustomer(User user);
}

