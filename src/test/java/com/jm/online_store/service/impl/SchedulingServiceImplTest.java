package com.jm.online_store.service.impl;

import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.SchedulingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Модульное, функциональное тестирование (JUnit5 + Mockito)
 */
@Slf4j
@SpringBootTest
public class SchedulingServiceImplTest {

    SchedulingServiceImpl schedulingService;

    private MailSenderService mailSenderService;

    private UserRepository userRepository;
    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    SchedulingServiceImpl schedulingServiceImpl = new SchedulingServiceImpl(mailSenderService,userRepository,stockRepository);

    @BeforeEach public void initMocks() {
        MockitoAnnotations.initMocks(this);
        User.DayOfWeekForStockSend dayOfWeek;
        when(userRepository.findByDayOfWeekForStockSend(dayOfWeek)).thenReturn("WEDNESDAY");
    }

    @Test
    void sendStocksToCustomers() throws InterruptedException {
        log.info("Before sending");

        mailSenderService.send(new String("1"),new String("2"),new String("3"),new String("4"));
        Thread.sleep(10000);
        log.info("After sending");

    }
}
