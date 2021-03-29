package com.jm.online_store.repository;

import com.jm.online_store.model.Review;
import com.jm.online_store.model.AnswerNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerNotificationsRepository extends JpaRepository<AnswerNotifications, Long> {

}
