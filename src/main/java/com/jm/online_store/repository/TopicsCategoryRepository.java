package com.jm.online_store.repository;

import com.jm.online_store.model.TopicsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicsCategoryRepository extends JpaRepository<TopicsCategory, Long> {
    TopicsCategory findByCategoryName(String name);

    boolean existsByCategoryName(String name);

    List<TopicsCategory> findAllByActualIsTrue();
}
