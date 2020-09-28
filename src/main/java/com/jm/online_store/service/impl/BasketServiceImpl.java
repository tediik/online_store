package com.jm.online_store.service.impl;

import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.BasketRepository;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public void addProductToBasket(User user, Long id) {
        User userToModify = userService.findById(user.getId()).get();
        Product productToAdd = productService
                .findProductById(id)
                .orElseThrow(ProductNotFoundException::new);
        SubBasket subBasket = SubBasket.builder()
                .product(productToAdd)
                .count(1)
                .build();
        List<SubBasket> userBasket = userToModify.getUserBasket();
        userBasket.add(subBasket);

    }
}
