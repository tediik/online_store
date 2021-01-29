package com.jm.online_store.service.interf;

import com.jm.online_store.model.RecentlyViewedProducts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RecentlyViewedProductsService {
    List<RecentlyViewedProducts> findAllRecentlyViewedProductsByUserId(Long id);

    List<RecentlyViewedProducts> findRecentlyViewedProductsByUserIdAndDateTimeBetween(Long id, LocalDate startDate, LocalDate endDate);

    void saveRecentlyViewedProducts(Long IdProduct, Long userId, LocalDateTime localDateTime);

    Boolean ProductExistsInTableOfUserId(Long productId, Long userId);

    void updateRecentlyViewedProducts(Long productId, Long userId, LocalDateTime localDateTime);
}
