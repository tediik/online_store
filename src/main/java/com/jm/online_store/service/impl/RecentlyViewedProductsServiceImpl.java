package com.jm.online_store.service.impl;

import com.jm.online_store.model.RecentlyViewedProducts;
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

    /**
     * Метод получает из базы коллекцию List сущностей RecentlyViewedProducts в которых есть idProduct которые просматривал юзер
     * @param id юзера которому принадлежат просмотренные товары
     * @return List<RecentlyViewedProducts>
     */
    @Override
    public List<RecentlyViewedProducts> findAllRecentlyViewedProductsByUserId(Long id) {
       return recentlyViewedProductsRepository.findRecentlyViewedProductsByUserId(id);
    }

    /**
     * Метод получает из базы сущность RecentlyViewedProducts по idProduct
     * @param idProduct просмотренного товара
     * @return
     */
    @Override
    public RecentlyViewedProducts findIdProduct(String idProduct) {
        return recentlyViewedProductsRepository.findByIdProduct(idProduct);
    }

    /**
     * Метод сохраняет в базе сущность RecentlyViewedProducts
     * @param recentlyViewedProducts сущность с idProduct
     * @return void
     */
    @Override
    public void saveRecentlyViewedProducts(RecentlyViewedProducts recentlyViewedProducts) {
        if (findIdProduct(recentlyViewedProducts.getIdProduct()) == null) {
            recentlyViewedProducts.setViewedDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            recentlyViewedProducts.setUser(userService.getCurrentLoggedInUser());
            recentlyViewedProducts.setIdProduct(recentlyViewedProducts.getIdProduct());
            recentlyViewedProductsRepository.save(recentlyViewedProducts);
        }
        else {
            RecentlyViewedProducts recentlyViewedProductsFromDb = findIdProduct(recentlyViewedProducts.getIdProduct());
            recentlyViewedProductsFromDb.setViewedDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            recentlyViewedProductsRepository.save(recentlyViewedProductsFromDb);
        }
    }
}
