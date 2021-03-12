package com.jm.online_store.repository;

import com.jm.online_store.model.PriceChangeNotifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceChangeNotificationRepository extends JpaRepository<PriceChangeNotifications, Long> {

    List<PriceChangeNotifications> findByCustomerId(Long id);

}
