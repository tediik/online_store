package com.jm.online_store.service.impl;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.MailSenderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Модульное, функциональное тестирование (JUnit5 + Mockito)
 */
@Slf4j
@SpringBootTest
@Data
@Service
class SchedulingServiceImplTest {

    @Autowired
    private SchedulingServiceImpl schedulingService;

    private MailSenderService mailSenderService;

    @Test
    public void sendStocksToCustomerTest() throws InterruptedException {
        MailSenderService mailSenderService = Mockito.mock(MailSenderService.class);

        String dayOfWeek = (LocalDate.now().getDayOfWeek().toString());
        String usersToSendStock = "test@mail.com";

        String currentAndFutureStocks = "Test title 1, test text 1";

        String messageBody = "prepareMessageBody(currentAndFutureStocks)";
        String messageSubject = "Внимание Акции!!!";
        log.info("Before sending");
        Thread.sleep(20000);
        mailSenderService.send("usersToSendStock", "messageSubject", "messageBody", "EMAIL_TYPE");
        log.info("After sending");

    }
}

/**
 * Метод создает messageBody для рассылки акций
 *
 * @param currentAndFutureStocks лист актуальных акций
 * @return Строка с текстом.
 */
//    private String prepareMessageBody(List<Stock> currentAndFutureStocks) {
//        StringBuilder messageForEmail = new StringBuilder();
//        for (Stock stock : currentAndFutureStocks) {
//            messageForEmail
//                    .append(stock.getStockTitle())
//                    .append("\n")
//                    .append(stock.getStockText())
//                    .append("\n")
//                    .append("\n")
//                    .append("Акция проходит с: ")
//                    .append(stock.getStartDate())
//                    .append(" по: ")
//                    .append(stock.getEndDate())
//                    .append("\n")
//                    .append("\n");
//        }
//        return messageForEmail.toString();
//    }


//        String messageSubject = "ЭТО ТЕСТ ОТПРАВКИ АКЦИИ!";
//        String messageBody = ("Тестовый заголовок" + "\n" + "Тестовое описание");
//        String EMAIL_TYPE = "Тип отправленного письма: акции";
//        log.info("before send");
//        MailSenderService mailSenderServiceMock = Mockito.mock(MailSenderService.class);
//        mailSenderServiceMock.send("fresh81@yandex.ru", "messageSubject", "messageBody", "EMAIL_TYPE");
//        Thread.sleep(20000);
//        log.info("after send");

