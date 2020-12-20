package com.jm.online_store.repository;

import com.jm.online_store.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStatus(Feedback.Status status);
}
