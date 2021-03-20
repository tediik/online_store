package com.jm.online_store.controller.rest.serviceWorker;

import com.jm.online_store.model.ConfirmationToken;
import com.jm.online_store.model.dto.CancelMailingDTO;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Рест контроллер для удаления всех товаров из рассылки
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/cancelAllMailing")
public class CancelAllMailingRestController {

    private final ConfirmationTokenRepository confirmTokenRepository;

    /**
     * Метод для получения и обработки данных на отписку от всех рассылок
     *
     * @return почту пользователя желающего отписаться от всех рассылок
     */
    @PostMapping("/data")
    public CancelMailingDTO getCancelAllMailingData(@RequestParam("token") String token) {

        ConfirmationToken confirmationToken = confirmTokenRepository.findByConfirmationToken(token);
        String email = confirmationToken.getUserEmail();
        CancelMailingDTO emailDTO = new CancelMailingDTO(email);
        return emailDTO;

    }

}
