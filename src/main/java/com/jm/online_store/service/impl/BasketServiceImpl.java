package com.jm.online_store.service.impl;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.ProductsNotFoundException;
import com.jm.online_store.exception.SubBasketNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.Order;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.SubBasket;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.BasketRepository;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.BasketService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.ProductInOrderService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class BasketServiceImpl implements BasketService {
    private final BasketRepository basketRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AddressService addressService;
    private final OrderService orderService;
    private final ProductInOrderService productInOrderService;

    /**
     * метод поиска сущности SubBasket.
     *
     * @param idBasket идентификатор SubBasket
     * @return SubBasket
     */
    @Override
    public SubBasket findBasketById(Long idBasket) {
        return basketRepository.findById(idBasket).orElseThrow(SubBasketNotFoundException::new);
    }

    /**
     * метод для получения списка товаров в корзине для авторизованного User.
     * @param sessionID номер id сессии для идентификации анонимного пользователя
     * @return List<SubBasket> список подкорзин
     */
    @Override
    public List<SubBasket> getBasket(String sessionID) {
        User authorityUser = userService.getCurrentLoggedInUser(sessionID);
        List<SubBasket> subBaskets = authorityUser.getUserBasket();
        int productCount;
        for (SubBasket subBasket : subBaskets) {
            productCount = productService.findProductById(subBasket.getProduct().getId())
                    .orElseThrow(ProductNotFoundException::new).getAmount();
            if (productCount < subBasket.getCount()) {
                subBasket.setCount(productCount);
                authorityUser.setUserBasket(subBaskets);
                userService.updateUser(authorityUser);
                if (productCount < 1) {
                    deleteBasket(subBasket,"");
                }
            }
        }
        return subBaskets;
    }

    /**
     * метод для обновления количества товара в подкорзине.
     *
     * @param subBasket подкорзина
     * @param difference счетчик увеличения/уменьшения кол-во товара
     * @return SubBasket
     */
    @Override
    public SubBasket updateBasket(SubBasket subBasket, int difference) {
        int count = subBasket.getCount();
        if (difference > 0) {
            if (subBasket.getProduct().getAmount() > count) {
                count += difference;
                subBasket.setCount(count);
            } else {
                subBasket.setCount(subBasket.getProduct().getAmount());
                basketRepository.saveAndFlush(subBasket);
                throw new ProductsNotFoundException("products run out");
            }
        } else {
            if (count > 1) {
                count += difference;
                subBasket.setCount(count);
            }
        }
        return basketRepository.saveAndFlush(subBasket);
    }

    /**
     * метод добавления SubBasket
     *
     * @param subBasket подкорзина
     * @return SubBasket
     */
    @Override
    public SubBasket addBasket(SubBasket subBasket) {
        return basketRepository.save(subBasket);
    }

    /**
     * метод для удаления сущности SubBasket(подкорзина) из списка подкорзин User.
     * @param sessionID номер id сессии для идентификации анонимного пользователя
     * @param subBasket подкорзина
     */
    @Override
    public void deleteBasket(SubBasket subBasket,String sessionID) {
        User authorityUser = userService.getCurrentLoggedInUser(sessionID);
        List<SubBasket> subBasketList = authorityUser.getUserBasket();
        subBasketList.remove(subBasket);
        authorityUser.setUserBasket(subBasketList);
        userService.updateUser(authorityUser);
        basketRepository.delete(subBasket);
    }

    /**
     * Method add product to basket. If product already in subBasket increases by 1
     * @param id - id of product to add
     * @param sessionID номер id сессии для идентификации анонимного пользователя
     * @return обновленный SubBasket {@link SubBasket}
     */
    @Override
    @Transactional
    public SubBasket addProductToBasket(Long id, String sessionID) {
        User userWhoseBasketToModify = userService.getCurrentLoggedInUser(sessionID);
        if (userWhoseBasketToModify == null) {
            throw new UserNotFoundException();
        }
        Product productToAdd = productService
                .findProductById(id)
                .orElseThrow(ProductNotFoundException::new);
        List<SubBasket> userBasket = userWhoseBasketToModify.getUserBasket();

       if(productToAdd.getAmount() <= 0) {
            throw new ProductsNotFoundException("В БД закончился данный продукт");
       } else {
           for (SubBasket basket : userBasket) {
               if (basket.getProduct().getId() == id) {
                   basket.setCount(basket.getCount() + 1);
               }
           }
           SubBasket subBasket = SubBasket.builder()
                   .product(productToAdd)
                   .count(1)
                   .build();
           basketRepository.save(subBasket);
           userBasket.add(subBasket);
           userService.updateUser(userWhoseBasketToModify);
           return subBasket;
       }
    }

    /**
     * метод для формирования заказа из корзины.
     * @param id идентификатор
     * @return {@link Order}
     */
    @Override
    public Order buildOrderFromBasket(Long id) {
        Address addressToAdd = addressService.findAddressById(id).orElseThrow(AddressNotFoundException::new);
        User authorityUser = userService.getCurrentLoggedInUser();
        List<SubBasket> subBasketList = authorityUser.getUserBasket();
        Product product;
        int count = 0;
        double sum = 0;
        Order order = new Order();
        long orderId = orderService.addOrder(order);
        for (SubBasket subBasket : subBasketList) {
            productInOrderService.addToOrder(subBasket.getProduct().getId(), orderId, subBasket.getCount());
            product = subBasket.getProduct();
            product.setAmount(product.getAmount() - subBasket.getCount());
            productService.saveProduct(product);
            count += subBasket.getCount();
            sum += subBasket.getProduct().getPrice() * subBasket.getCount();
        }
        order.setDateTime(LocalDateTime.now());
        order.setAmount((long) count);
        order.setOrderPrice(sum);
        order.setStatus(Order.Status.INCARTS);
        order.setAddress(addressService.findAddressById(addressToAdd.getId())
                 .orElseThrow(AddressNotFoundException::new));
        Set<Order> orderSet = authorityUser.getOrders();
        orderSet.add(order);
        authorityUser.setOrders(orderSet);
        orderService.updateOrder(order);
        authorityUser.setUserBasket(new ArrayList<>());
        userService.updateUser(authorityUser);
        return order;
    }
}
