package com.jm.online_store.controller.simple;

import com.jm.online_store.enums.ConfirmReceiveEmail;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.service.impl.ProductServiceImpl;
import com.jm.online_store.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/cancelMailing")
public class CancelMailingController {

    private final ConfirmationTokenRepository confirmTokenRepository;
    private final UserServiceImpl userService;
    private final ProductServiceImpl productServiceImpl;

    /**
     * Метод принимающий ссылку на отписку от рассылки на изменение цены
     *
     * @return возвращает страницу для подтверждения отписки
     */
    @GetMapping("/{token}/{productId}")
    public String getCancelMailingPage() {
        return "/cancelMailing";
    }
    /**
     * Метод принимающий ссылку на отписку от рассылки на изменение цены
     *
     * @param email     почта  пользователя подписанного на продукт
     * @param productId id продукта
     * @return возвращает страницу подтверждающую отписку
     */
    @PostMapping("/delete")
    public String removeProductFromMailing(@RequestParam String email, @RequestParam String productId) {
        productServiceImpl.deleteProductPriceChangeById(email, Long.parseLong(productId));
        return "/cancelMailingOK";
    }

    /**
     * Метод принимающий ссылку на отписку от всех рассылок
     *
     * @return возвращает страницу для подтверждения отписки
     */
    @GetMapping("cancelMailingAll/{token}/{productId}")
    public String getCancelAllMailingPage() {
        return "/cancelAllMailing";
    }

    /**
     * Метод принимающий ссылку на отписку от всех рассылок
     *
     * @param email почта пользователя желающего отписаться от всех рассылок
     * @return возвращает страницу подтверждающую отписку
     */
    @PostMapping("/deleteAll")
    public String removeProductFromMailing(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        user.get().setConfirmReceiveEmail(ConfirmReceiveEmail.NO_ACTIONS);
        productServiceImpl.deleteAllByEmail(email);
        return "/cancelMailingOK";
    }
}


