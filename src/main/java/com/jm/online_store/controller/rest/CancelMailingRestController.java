package com.jm.online_store.controller.rest;

import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.CancelMailingDTO;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.service.impl.ProductServiceImpl;
import com.jm.online_store.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

/**
 * Рест контроллер для удаления товаров из рассылки
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/cancelMailing")
public class CancelMailingRestController {

    private final ConfirmationTokenRepository confirmTokenRepository;
    private final UserServiceImpl userService;
    private final ProductServiceImpl productServiceImpl;

    /**
     * Метод для получения и обработки данных на отписку от рассылки на изменение цены
     * @param id  идентификатор продукта
     * @param token токен продукта
     * @return возвращает cancelMailingDTO
     */
    @PostMapping("/data")
    public CancelMailingDTO getCancelMailingData(@RequestParam("id") String id, @RequestParam("token") String token) {

        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        String email = confirmationToken.getUserEmail();
        String password = confirmationToken.getUserPassword();
        Optional<Product> product = productServiceImpl.findProductById(Long.parseLong(id));
        String productName = product.get().getProduct();
        CancelMailingDTO cancelMailingDTO = new CancelMailingDTO(id,email,password,productName);
        return cancelMailingDTO;

    }

}
