package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.MailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

/**
 * Модульное, функциональное тестирование (JUnit5 + Mockito)
 */
@Slf4j
@SpringBootTest
@Service
class SchedulingServiceImplTest {

//    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    @Autowired

    @Test
    public void sendStocksToCustomerTest() throws InterruptedException {
        String messageSubject = "ЭТО ТЕСТ ОТПРАВКИ АКЦИИ!";
        String messageBody = ("Тестовый заголовок" + "\n" + "Тестовое описание");
        String EMAIL_TYPE = "Тип отправленного письма: акции";
        log.info("before send");
        MailSenderService mailSenderServiceMock = Mockito.mock(MailSenderService.class);
        mailSenderServiceMock.send("fresh81@yandex.ru", messageSubject, messageBody, EMAIL_TYPE);
        Thread.sleep(20000);
        log.info("after send");
    }
}