package com.jm.online_store.controller.simple;

import com.jm.online_store.service.interf.CommentService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.ReviewService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CustomerController контроллер для пользователя с ролью "Customer"
 */
@AllArgsConstructor
@Controller
@RequestMapping("/customer")
@Slf4j
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final FavouritesGroupService favouritesGroupService;

    /**
     * метод получения данных зарегистрированного пользователя.
     * формирование модели для вывода в "view"
     * модель данных, построенных на основе зарегистрированного User
     */
    @GetMapping
    public String getUserProfile() {
        return "customer-page";
    }


    @GetMapping("/basket")
    public String getUserBasket() {
        return "basket-page";
    }

}
