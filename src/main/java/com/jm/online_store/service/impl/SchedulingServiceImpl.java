package com.jm.online_store.service.impl;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.MailSenderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Класс планировщик заданий. В соответствии с заданным планом выполняет определенные действия.
 */
@AllArgsConstructor
@Slf4j
@Service
public class SchedulingServiceImpl implements com.jm.online_store.service.interf.SchedulingService {

    private final MailSenderService mailSenderService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    private final String EMAIL_TYPE = "Stock sender";

    @Override
    @Scheduled(cron = "${emailStockSending.delay}")
    public void sendStocksToCustomers() {
        String dayOfWeek = LocalDate.now().getDayOfWeek().toString();
        List<User> usersToSendStock = userRepository.findByDayOfWeekForStockSend(dayOfWeek);
        LocalDate beginningOfPeriod = LocalDate.now().minusDays(7L);
        LocalDate endOfPeriod = LocalDate.now().plusDays(7L);
        List<Stock> currentAndFutureStocks = stockRepository
                .findAllByStartDateBetweenAndEndDateIsAfter(beginningOfPeriod, endOfPeriod, LocalDate.now());

        String messageBody = prepareMessageBody(currentAndFutureStocks);
        String messageSubject = "Внимание Акции!!!";

        if (usersToSendStock.size() != 0) {
            for (User user : usersToSendStock) {

                mailSenderService.send(user.getEmail(), messageSubject, messageBody, EMAIL_TYPE);
                log.debug("Stock message was sent to {} with email {}", user, user.getEmail());
            }
            log.debug("{} stock emails were sent", usersToSendStock.size());
        } else {
            log.debug("There are no users in db that satisfy conditions");
        }
    }

}
