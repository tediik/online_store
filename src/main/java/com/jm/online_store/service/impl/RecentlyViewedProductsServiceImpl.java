package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.repository.RecentlyViewedProductsRepository;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.RecentlyViewedProductsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RecentlyViewedProductsServiceImpl implements RecentlyViewedProductsService {
    private final RecentlyViewedProductsRepository recentlyViewedProductsRepository;
    private final UserService userService;
    private final ProductService productService;

    /**
     * Метод получает из базы список сущностей RecentlyViewedProducts,
     * в которых есть Product, который просматривал пользователь
     * @param id юзера которому принадлежат просмотренные товары
     * @return List<RecentlyViewedProducts>
     */
    @Override
    public List<RecentlyViewedProducts> findAllRecentlyViewedProductsByUserId(Long id) {
       return recentlyViewedProductsRepository.findRecentlyViewedProductsByUserId(id);
    }

    /**
     * Метод проверяет есть ли данная запись в таблице
     * сущности recentlyViewedProducts по productId и одновременно по userId
     * @param productId просмотренного то вара
     * @return
     */
    @Override
    public Boolean ProductExistsInTableOfUserId(Long productId, Long userId) {
        return recentlyViewedProductsRepository.existsRecentlyViewedProductsByProduct_IdAndUser_Id(productId, userId);
    }

    @Override
    public void updateRecentlyViewedProducts(Long productId, Long userId, LocalDateTime localDateTime) {
        RecentlyViewedProducts products = recentlyViewedProductsRepository.findRecentlyViewedProductsByProduct_IdAndUser_Id(productId, userId);
        products.setDateTime(localDateTime);
        recentlyViewedProductsRepository.save(products);
    }

    /**
     * Метод получает из базы отфильтрованный по промежутку времени
     * список сущностей RecentlyViewedProducts,
     * в которых есть Product, который просматривал пользователь
     * @param id        юзера которому принадлежат просмотренные товары
     * @param startDate - {@link LocalDate} начало промежутка
     * @param endDate   - {@link LocalDate} конец промежутка
     * @return List<RecentlyViewedProducts>
     */
    @Override
    public List<RecentlyViewedProducts> findRecentlyViewedProductsByUserIdAndDateTimeBetween(Long id, LocalDate startDate, LocalDate endDate) {
        return recentlyViewedProductsRepository.findRecentlyViewedProductsByUserIdAndDateTimeBetween(id, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    /**
     * Метод сохраняет в базе сущность RecentlyViewedProducts
     * @param IdProduct, userId
     * @return void
     */
    @Override
    public void saveRecentlyViewedProducts(Long IdProduct, Long userId, LocalDateTime localDateTime) {
        RecentlyViewedProducts recentlyViewedProductsNew = new RecentlyViewedProducts();
        recentlyViewedProductsNew.setProduct(productService.findProductById(IdProduct).orElse(new Product()));
        recentlyViewedProductsNew.setUser(userService.findUserById(userId));
        recentlyViewedProductsNew.setDateTime(localDateTime);
        recentlyViewedProductsRepository.save(recentlyViewedProductsNew);
    }
}
