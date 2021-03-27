package com.jm.online_store.repository;

import com.jm.online_store.model.Review;
import com.jm.online_store.model.ReviewNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewNotificationsRepository extends JpaRepository<ReviewNotifications, Long> {

}
