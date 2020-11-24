package com.jm.online_store.repository;

import com.jm.online_store.model.Comment;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByProductId(Long productId);

    List<Comment> findAllByReviewId(Long reviewId);

    List<Comment> findCommentsByCustomer(User user);
}
