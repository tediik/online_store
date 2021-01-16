package com.jm.online_store.service.impl;

import com.jm.online_store.model.Product;
import com.jm.online_store.model.RecentlyViewedProducts;
import com.jm.online_store.repository.RecentlyViewedProductsRepository;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.RecentlyViewedProductsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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
     * сущности recentlyViewedProducts по productId
     * @param productId просмотренного то вара
     * @return
     */
    @Override
    public Boolean ProductExistsInTable(Long productId) {
      return recentlyViewedProductsRepository.existsRecentlyViewedProductsByProduct_Id(productId);
    }

    /**
     * Метод сохраняет в базе сущность RecentlyViewedProducts
     * @param IdProduct
     * @return void
     */
    @Override
    public void saveRecentlyViewedProducts(Long IdProduct) {
        RecentlyViewedProducts recentlyViewedProductsNew = new RecentlyViewedProducts();
        recentlyViewedProductsNew.setProduct(productService.findProductById(IdProduct).orElse(new Product()));
            recentlyViewedProductsNew.setUser(userService.getCurrentLoggedInUser());
            recentlyViewedProductsRepository.save(recentlyViewedProductsNew);
    }
}
