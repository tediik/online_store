package com.jm.online_store.service.interf;

import com.jm.online_store.model.RecentlyViewedProducts;
import java.util.List;

public interface RecentlyViewedProductsService {
    List<RecentlyViewedProducts> findAllRecentlyViewedProductsByUserId(Long id);
    void saveRecentlyViewedProducts(Long IdProduct, Long userId);
    Boolean ProductExistsInTableOfUserId(Long productId, Long userId);
}
