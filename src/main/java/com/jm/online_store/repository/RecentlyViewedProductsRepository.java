package com.jm.online_store.repository;

import com.jm.online_store.model.Order;
import com.jm.online_store.model.RecentlyViewedProducts;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecentlyViewedProductsRepository extends JpaRepository<RecentlyViewedProducts, Long> {
    List<RecentlyViewedProducts> findRecentlyViewedProductsByUserId(Long id);

    Boolean existsRecentlyViewedProductsByProduct_IdAndUser_Id(Long productId, Long userId);

    List<RecentlyViewedProducts> findRecentlyViewedProductsByUserIdAndDateTimeBetween(Long id, @NonNull LocalDateTime startDate, @NonNull LocalDateTime endDate);
}
