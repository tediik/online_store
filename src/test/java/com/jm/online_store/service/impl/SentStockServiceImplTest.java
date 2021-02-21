package com.jm.online_store.service.impl;

import com.jm.online_store.exception.SentStockNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.repository.SentStockRepository;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * класс тестирования для методов SentStockService
 */
public class SentStockServiceImplTest {
    @Mock
    private SentStockRepository sentStockRepository;
    @Mock
    private StockService stockService;
    @Mock
    private UserService userService;
    @InjectMocks
    private SentStockServiceImpl sentStockService;
    List<SentStock> sentStockList;
    SentStock sentStock1;
    SentStock sentStock2;
    SentStock sentStock3;
    User testUser;
    Stock testStock;

    public SentStockServiceImplTest() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        sentStockList = new ArrayList<>();

        testUser = new User("testuser@mail.ru", "1");
        testUser.setId(1L);

        testStock = Stock.builder()
                .id(1L)
                .stockText("testText")
                .stockTitle("testTitle")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();

        sentStock1 = SentStock.builder()
                .user(testUser)
                .sentDate(LocalDate.now())
                .stock(testStock)
                .build();
        sentStock2 = SentStock.builder()
                .user(testUser)
                .sentDate(LocalDate.now().plusDays(1L))
                .stock(testStock)
                .build();
        sentStock3 = SentStock.builder()
                .user(testUser)
                .sentDate(LocalDate.now().plusDays(2L))
                .stock(testStock)
                .build();

        sentStockList.add(sentStock1);
        sentStockList.add(sentStock2);
        sentStockList.add(sentStock3);
    }

    @AfterEach
    void tearDown() {
        sentStock1 = null;
        sentStock2 = null;
        sentStock3 = null;
        sentStockList = null;
        testUser = null;
        testStock = null;
    }

    /**
     * тест метода поиска акций в интервале
     */
    @Test
    public void findStocksByIntreval() {
        when(sentStockRepository.findAllBySentDateAfterAndSentDateBefore(LocalDate.now().minusDays(1L), LocalDate.now().plusDays(3L)))
                .thenReturn(sentStockList);
        assertEquals(sentStockList, sentStockService.findAllByInterval(LocalDate.now(), LocalDate.now().plusDays(2L)));
    }

    /**
     * тест поиска отправленных акций, пробрасывает исключение
     */
    @Test
    public void findByIntervalReturnExeption() {
        when(sentStockRepository.findAllBySentDateAfterAndSentDateBefore(LocalDate.now().minusDays(3L), LocalDate.now().minusDays(1L)))
                .thenReturn(new ArrayList<>());
        assertThrows(SentStockNotFoundException.class, () -> sentStockService.findAllByInterval(LocalDate.now().minusDays(2L), LocalDate.now().minusDays(2L)));
    }

    /**
     * тест метода добавления информации об отправиленной акции
     */
    @Test
    public void addSentStock() {
        when(sentStockRepository.save(sentStock1)).thenReturn(sentStock1);
        when(stockService.findStockById(any())).thenReturn(testStock);
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        SentStock testSentStock = sentStockService.addSentStock(sentStock1);
        assertNotNull(testSentStock);
        verify(sentStockRepository, times(1)).save(sentStock1);
    }

    /**
     * тест метода получения HashMap для дальнейшего использования
     */
    @Test
    public void getSentStocksMaps() {
        Map<LocalDate, Long> testMap = new HashMap<>();
        testMap.put(LocalDate.now(), 1L);
        when(sentStockRepository
                .findAllBySentDateAfterAndSentDateBefore(
                        LocalDate.now().minusDays(1L),
                        LocalDate.now().plusDays(1L)))
                .thenReturn(sentStockList.subList(0, 1));
        assertEquals(testMap, sentStockService.getSentStocksMap(LocalDate.now(), LocalDate.now()));
    }

    @Test
    public void addSentStockExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> sentStockService.addSentStock(sentStock1));
    }
}
