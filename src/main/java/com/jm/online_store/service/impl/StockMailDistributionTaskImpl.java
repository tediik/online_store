package com.jm.online_store.service.impl;

import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.StockMailDistributionTask;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@AllArgsConstructor
@Slf4j
@Service
public class StockMailDistributionTaskImpl implements StockMailDistributionTask {

    private final MailSenderService mailSenderService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final CommonSettingsService commonSettingsService;

    private final String EMAIL_TYPE = "Stock sender";

    /**
     * Метод выполняется с регулярностью заданным параметром ${emailStockSending.delay} в файле application.yml
     * берем текущий день, делаем выборку среди всех {@link User} у кого в поле DayOfWeekForStockSend
     * текущий день недели отправляем заранее сформированный email из текущих и будущих акций.
     * Акции отбирабтся следующим условием:
     * - Задается период +- 7 дней от текущего.
     * - день старта акции должен попадать в этот промежуток и день окончания акции должен быть после текущего дня.
     */
    @SneakyThrows
    @Override
    public void run() {
        User.DayOfWeekForStockSend dayOfWeek = User.DayOfWeekForStockSend.valueOf(LocalDate.now().getDayOfWeek().toString());
        List<User> usersToSendStock = userRepository.findByDayOfWeekForStockSend(dayOfWeek);
        List<Stock> currentAndFutureStocks = stockRepository
                .findAllByStartDateBetweenAndEndDateIsAfter(LocalDate.now().minusDays(7L), LocalDate.now().plusDays(7L), LocalDate.now());

        if (usersToSendStock.size() != 0) {
            for (User user : usersToSendStock) {
                String messageSubject = user.getFirstName() + ", мы подобрали вам список актуальных акций!!!";
                String messageBody = prepareMessageBody(currentAndFutureStocks, user);
                mailSenderService.sendHtmlMessage(user.getEmail(), messageSubject, messageBody, "stock mail distribution");
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
     * @param user пользователь для которого подготавливается рассылка
     * @return Строка с текстом.
     */
    private String prepareMessageBody(List<Stock> currentAndFutureStocks, User user) {
        String templateBody = commonSettingsService.getSettingByName("stock_email_distribution_template").getTextValue();
        String messageBody = templateBody.replace("@@user@@", user.getFirstName());
        StringBuilder messageForEmail = new StringBuilder();
        for (Stock stock : currentAndFutureStocks) {
            messageForEmail
                    .append(stock.getStockTitle())
                    .append("<br>")
                    .append(stock.getStockText())
                    .append("<br>")
                    .append("Акция проходит с: ")
                    .append(stock.getStartDate())
                    .append(" по: ")
                    .append(stock.getEndDate())
                    .append("<br>")
                    .append("<br>")
                    .append("<hr>");
        }
        return messageBody.replace("@@stockList@@", messageForEmail);
    }
}
