package com.jm.online_store.repository;

import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentlyViewedProductsRepository extends JpaRepository<RecentlyViewedProducts, Long> {
    List<RecentlyViewedProducts> findRecentlyViewedProductsByCustomer(User user);
    List<RecentlyViewedProducts> findAll();
    void saveRecentlyViewedProducts(RecentlyViewedProducts recentlyViewedProducts, String idProducts);
}
