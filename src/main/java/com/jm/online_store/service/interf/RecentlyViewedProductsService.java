package com.jm.online_store.service.interf;

import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import java.util.List;

public interface RecentlyViewedProductsService {
    List<RecentlyViewedProducts> findRecentlyViewedProductsByCustomer(User user);
    List<RecentlyViewedProducts> findAll();
    void saveRecentlyViewedProducts(RecentlyViewedProducts recentlyViewedProducts, String idProducts);
}
