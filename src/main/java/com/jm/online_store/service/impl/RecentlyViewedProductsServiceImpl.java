package com.jm.online_store.service.impl;

import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.RecentlyViewedProductsRepository;
import com.jm.online_store.service.interf.RecentlyViewedProductsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class RecentlyViewedProductsServiceImpl implements RecentlyViewedProductsService {
    private final RecentlyViewedProductsRepository recentlyViewedProductsRepository;
    private final UserService userService;
    //private final RecentlyViewedProducts recentlyViewedProducts;

    @Override
    public List<RecentlyViewedProducts> findRecentlyViewedProductsByCustomer(User user) {

    }

    @Override
    public List<RecentlyViewedProducts> findAll() {

    }

    @Override
    public void saveRecentlyViewedProducts(RecentlyViewedProducts recentlyViewedProducts, String idProducts) {
        recentlyViewedProducts.setViewedDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        recentlyViewedProducts.setUser(userService.getCurrentLoggedInUser());
        recentlyViewedProducts.setIdProduct(idProducts);
        recentlyViewedProductsRepository.saveRecentlyViewedProducts(recentlyViewedProducts, idProducts);
    }
}
