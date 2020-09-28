package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.BasketRepository;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final ProductService productService;
    private final UserService userService;

    @Override
    public SubBasket findBasketById(Long idBasket) {
        return basketRepository.findById(idBasket).get();
    }

    @Override
    public SubBasket updateBasket(SubBasket subBasket) {
        return basketRepository.saveAndFlush(subBasket);
    }

    @Override
    public SubBasket addBasket(SubBasket subBasket) {
        return basketRepository.save(subBasket);
    }

    @Override
    public void deleteBasket(SubBasket subBasket) {
        basketRepository.delete(subBasket);
    }

    /**
     * Method add product to basket. If product already in subBasket increases by 1
     * @param user - principal
     * @param id - id of product to add
     */
    @Override
    @Transactional
    public void addProductToBasket(User user, Long id) {
        User userWhoseBasketToModify = userService.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        Product productToAdd = productService
                .findProductById(id)
                .orElseThrow(ProductNotFoundException::new);
        List<SubBasket> userBasket = userWhoseBasketToModify.getUserBasket();
        for (SubBasket basket : userBasket) {
            if (basket.getProduct().getId().equals(id)) {
                basket.setCount(basket.getCount() + 1);
                return;
            }
        }
        SubBasket subBasket = SubBasket.builder()
                .product(productToAdd)
                .count(1)
                .build();
        basketRepository.save(subBasket);
        userBasket.add(subBasket);
        userService.updateUser(userWhoseBasketToModify);
    }
}
