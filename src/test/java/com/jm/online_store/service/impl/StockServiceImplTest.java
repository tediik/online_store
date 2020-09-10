package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * класс тестирования для методов StockService.
 */
class StockServiceImplTest {
    private final StockRepository stockRepository = Mockito.mock(StockRepository.class);
    private final StockService stockService = new StockServiceImpl(stockRepository);

    List<Stock> stockList;
    Stock firstStock;
    Stock secondStock;
    Stock thirdStock;

    @BeforeEach
    void setUp() {
        stockList = new ArrayList<>();
        firstStock = Stock.builder()
                .startDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(12L))
                .stockTitle("1")
                .stockText("future")
                .build();
        firstStock.setId(1L);

        secondStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(5L))
                .endDate(LocalDate.now().plusDays(3L))
                .stockTitle("2")
                .stockText("now")
                .build();
        secondStock.setId(2L);

        thirdStock = Stock.builder()
                .startDate(LocalDate.now().minusDays(20L))
                .endDate(LocalDate.now().minusDays(5L))
                .stockTitle("3")
                .stockText("past")
                .build();
        thirdStock.setId(3L);

        stockList.add(firstStock);
        stockList.add(secondStock);
        stockList.add(thirdStock);
    }

    @AfterEach
    void tearDown() {
        firstStock = null;
        secondStock = null;
        thirdStock = null;
        stockList = null;
    }

    /**
     * тест метода поиска всех акций и определения их статуса.
     */
    @Test
    void findAll() {
        when(stockRepository.findAll()).thenReturn(stockList);
        List<Stock> resultStockList = stockService.findAll();
        assertEquals(Stock.StockType.FUTURE, resultStockList.get(0).getStockType());
        assertEquals(Stock.StockType.CURRENT, resultStockList.get(1).getStockType());
        assertEquals(Stock.StockType.PAST, resultStockList.get(2).getStockType());
    }

    /**
     * тест метода поиска акции по идентификатору.
     */
    @Test
    void findStockByIdThenReturnOneElement() {
        when(stockRepository.findById(1L)).thenReturn(Optional.ofNullable(firstStock));
        Stock resultStock = stockService.findStockById(1L);
        assertEquals(firstStock, resultStock);
    }

    /**
     * тест метода поиска акции по идентификатору - пробрасывает исключение.
     */
    @Test
    void findStockByNoIdThenReturnExeption() {
        when(stockRepository.findById(4L)).thenReturn(null);
        try {
            stockService.findStockById(4L);
        } catch (StockNotFoundException e) {
            return;
        }
        Assert.fail();
    }

    /**
     * тест поиска действующих акций.
     */
    @Test
    void findCurrentStocks() {
        List<Stock> currentList = new ArrayList<>();
        currentList.add(secondStock);
        when(stockRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateEquals(LocalDate.now(), LocalDate.now(), null))
                .thenReturn(currentList);
        assertEquals(currentList, stockService.findCurrentStocks());
    }

    /**
     * тест поиска действующих акций - проброска исключения при пустом списке.
     */
    @Test
    void findCurrentStocks_throwExeption() {
        when(stockRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateEquals(LocalDate.now(), LocalDate.now(), null))
                .thenReturn(new ArrayList<>());
        try {
            stockService.findCurrentStocks();
        } catch (StockNotFoundException e) {
            return;
        }
        Assert.fail();
    }

    /**
     * тест поиска будущих акций.
     */
    @Test
    void findFutureStocks() {
        List<Stock> futureList = new ArrayList<>();
        futureList.add(firstStock);
        when(stockRepository.findByStartDateAfter(LocalDate.now())).thenReturn(futureList);
        assertEquals(futureList, stockService.findFutureStocks());
    }

    /**
     * тест поиска будущих акций - проброска исключения при пустом списке.
     */
    @Test()
    void findFutureStocks_throwExeption() {
        when(stockRepository.findByStartDateAfter(LocalDate.now())).thenReturn(new ArrayList<>());
        try {
            stockService.findFutureStocks();
        } catch (StockNotFoundException e) {
            return;
        }
        Assert.fail();
    }

    /**
     * тест поиска прошедших акций.
     */
    @Test
    void findPastStocks() {
        List<Stock> pastList = new ArrayList<>();
        pastList.add(thirdStock);
        when(stockRepository.findByEndDateBefore(LocalDate.now())).thenReturn(pastList);
        assertEquals(pastList, stockService.findPastStocks());
    }

    /**
     * тест поиска прошедших акций - проброска исключения при пустом списке.
     */
    @Test
    void findPastStocks_throwExeption() {
        when(stockRepository.findByEndDateBefore(LocalDate.now())).thenReturn(new ArrayList<>());
        try {
            stockService.findPastStocks();
        } catch (StockNotFoundException e) {
            return;
        }
        Assert.fail();
    }

    /**
     * тест обновления акции.
     */
    @Test
    void updateStock() {
        Stock updateStock = new Stock();
        updateStock.setId(1L);
        updateStock.setStockText("updateText");
        updateStock.setStockTitle("updateTitle");
        updateStock.setStartDate(LocalDate.now().minusDays(2L));
        updateStock.setEndDate(LocalDate.now().minusDays(1L));
        updateStock.setStockImg("updateIMG");
        when(stockRepository.findById(1L)).thenReturn(Optional.ofNullable(firstStock));
        stockService.updateStock(updateStock);
        assertEquals(1L, firstStock.getId());
        assertEquals("updateText", firstStock.getStockText());
        assertEquals("updateTitle", firstStock.getStockTitle());
        assertEquals(LocalDate.now().minusDays(2L), firstStock.getStartDate());
        assertEquals(LocalDate.now().minusDays(1L), firstStock.getEndDate());
        assertEquals("updateIMG", firstStock.getStockImg());
    }
}