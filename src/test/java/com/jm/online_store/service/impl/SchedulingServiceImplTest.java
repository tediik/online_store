package com.jm.online_store.service.impl;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.MailSenderService;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Модульное, функциональное тестирование (JUnit5 + Mockito)
 */
@Service
class SchedulingServiceImplTest {
    @Mock
    private MailSenderService mailSenderServiceMock;
//    private StockRepository stockRepository;
//    private String dayOfWeek = "tuesday";
    private User userToSendStock;
    private Stock currentAndFutureStocksMock;

    public SchedulingServiceImplTest() {
        MockitoAnnotations.initMocks(this);

    }

//    @BeforeTestClass
//    public void beforeTests() {
////        userToSendStock = Mockito.mock(User.class);
////        currentAndFutureStocksMock = Mockito.mock(Stock.class);
////        mailSenderServiceMock = Mockito.mock(MailSenderService.class);
//    }

   @Test
    public void sendStocksToCustomers() {
       String messageBody = "title1" + "\n" + "text1";
       String messageSubject = "Внимание Акции!!!";
       String EMAIL_TYPE = "Stock sender";
       mailSenderServiceMock.send("fresh81@yandex.ru", messageSubject, messageBody, EMAIL_TYPE);
    }

//    @Test
//    void messageReturnTrueTest() {
//        StringBuilder messageForEmail = new StringBuilder();
//        messageForEmail
//                .append("some test title")
//                .append("\n")
//                .append("some test text")
//                .append("\n")
//                .append("\n")
//                .append("Акция проходит с: ")
//                .append(2020 - 01 - 02)
//                .append(" по: ")
//                .append(2020 - 02 - 01)
//                .append("\n")
//                .append("\n");
//
//        assertThat(messageForEmail).isNotEmpty();
//    }


    //1. день рассылки
    //2. Пользователь кому отсылать
    //3. Текущие акции
    //4. Тип акции
    //5. Тема акции
    //6. Тело акции
}