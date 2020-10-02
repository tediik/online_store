package com.jm.online_store.repository;

import com.jm.online_store.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query("SELECT DISTINCT t.topicCategory FROM Topic t")
    List<String> findAllCategories();

    @Query("SELECT t.topicName FROM Topic t WHERE t.topicCategory = :category")
    List<String> findTopicsByCategory(@Param("category") String category);
}
