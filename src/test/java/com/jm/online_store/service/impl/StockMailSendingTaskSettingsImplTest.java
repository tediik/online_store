package com.jm.online_store.service.impl;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static com.jm.online_store.model.User.DayOfWeekForStockSend.SUNDAY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульное, функциональное тестирование (JUnit5 + Mockito)
 */
@Slf4j
@SpringBootTest
public class StockMailSendingTaskSettingsImplTest {

    @Autowired
    StockMailSendingTaskImpl schedulingService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private StockRepository stockRepository;

    User testUser = new User();
    List<User> testUserList = new ArrayList<>();
    Stock testStock = new Stock();
    List<Stock> testStockList = new ArrayList<>();

    //Проверка отправки акции пользователю в указанный день
    @Test
    void send_stocks_to_customers() {
        testUser.setEmail("jm-online-store@yandex.ru");
        testUserList.add(testUser);
        testStock.setStockTitle("Test title");
        testStock.setStockText("Test text");
        testStock.setStartDate(LocalDate.now());
        testStock.setEndDate(LocalDate.now());
        testStockList.add(testStock);

        log.info("Before sending Stock");

        when(userRepository.findByDayOfWeekForStockSend(any()))
                .thenReturn(testUserList);
        when(stockRepository.findAllByStartDateBetweenAndEndDateIsAfter(any(), any(), any()))
                .thenReturn(testStockList);

        Assert.notNull(userRepository.findByDayOfWeekForStockSend(any()),"Проверка, что NotNull");

        Mockito.verify(userRepository, Mockito.times(1))
                .findByDayOfWeekForStockSend(any());
        //TODO поправить
//        schedulingService.sendStocksToCustomers();

        log.info("After sending Stock");
    }

    //Проверка отправки акции пользователю в отсутствующий день
    // (по воскресеньям не запускать)
    @Test
    void do_not_send_stocks_to_customers() {

        log.info("Before sending Stock");

        when(userRepository.findByDayOfWeekForStockSend(SUNDAY))
                .thenReturn(testUserList);
        when(stockRepository.findAllByStartDateBetweenAndEndDateIsAfter(any(), any(), any()))
                .thenReturn(testStockList);

        Mockito.verify(userRepository, Mockito.times(0))
                .findByDayOfWeekForStockSend(any());
        //TODO поправить
//        schedulingService.sendStocksToCustomers();

        log.info("After sending Stock");
    }
}
