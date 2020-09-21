package com.jm.online_store.service.impl;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.SchedulingService;
import com.jm.online_store.service.interf.SentStockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Класс планировщик заданий. В соответствии с заданным планом выполняет определенные действия(задания).
 */
@AllArgsConstructor
@Slf4j
@Service
public class SchedulingServiceImpl implements SchedulingService {

    private final MailSenderService mailSenderService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final SentStockService sentStockService;

    private final String EMAIL_TYPE = "Stock sender";

    /**
     * Метод выполняется с регулярностью заданным параметром ${emailStockSending.delay} в файле application.yml
     * берем текущий день, делаем выборку среди всех {@link User} у кого в поле DayOfWeekForStockSend
     * текущий день недели отправляем заранее сформированный email из текущих и будущих акций.
     * Акция {@link Stock} и пользователь {@link User}, которому она была отправлена
     * связываются при помощи сущности {@link SentStock}
     * Акции отбирабтся следующим условием:
     * - Задается период +- 7 дней от текущего.
     * - день старта акции должен попадать в этот промежуток и день окончания акции должен быть после текущего дня.
     */
    @Override
    @Scheduled(cron = "${emailStockSending.delay}")
    public void sendStocksToCustomers() {
        User.DayOfWeekForStockSend dayOfWeek = User.DayOfWeekForStockSend.valueOf(LocalDate.now().getDayOfWeek().toString());
        List<User> usersToSendStock = userRepository.findByDayOfWeekForStockSend(dayOfWeek);
        List<Stock> currentAndFutureStocks = stockRepository
                .findAllByStartDateBetweenAndEndDateIsAfter(LocalDate.now().minusDays(7L), LocalDate.now().plusDays(7L), LocalDate.now());

        String messageBody = prepareMessageBody(currentAndFutureStocks);
        String messageSubject = "Внимание Акции!!!";

        if (usersToSendStock.size() != 0) {
            for (User user : usersToSendStock) {
                for (Stock stock : currentAndFutureStocks) {
                    SentStock sentStock = SentStock.builder()
                            .user(user)
                            .stock(stock)
                            .sentDate(LocalDate.now())
                            .build();
                    try {
                        sentStockService.addSentStock(sentStock);
                    } catch (UserNotFoundException e) {
                        log.debug("Cannot add Stock {} for user {}", stock.getId(), user.getEmail());
                    }
                }
                mailSenderService.send(user.getEmail(), messageSubject, messageBody, EMAIL_TYPE);
                log.debug("Stock message was sent to {} with email {}", user, user.getEmail());
            }
            log.debug("{} stock emails were sent", usersToSendStock.size());
        } else {
            log.debug("There are no users in db with DayOfWeekForStockSend field equals {}", dayOfWeek);
        }
    }

    /**
     * Метод создает messageBody для рассылки акций
     *
     * @param currentAndFutureStocks лист актуальных акций
     * @return Строка с текстом.
     */
    private String prepareMessageBody(List<Stock> currentAndFutureStocks) {
        StringBuilder messageForEmail = new StringBuilder();
        for (Stock stock : currentAndFutureStocks) {
            messageForEmail
                    .append(stock.getStockTitle())
                    .append("\n")
                    .append(stock.getStockText())
                    .append("\n")
                    .append("\n")
                    .append("Акция проходит с: ")
                    .append(stock.getStartDate())
                    .append(" по: ")
                    .append(stock.getEndDate())
                    .append("\n")
                    .append("\n");
        }
        return messageForEmail.toString();
    }
}
