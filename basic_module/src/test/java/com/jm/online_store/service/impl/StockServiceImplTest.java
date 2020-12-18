//package com.jm.online_store.service.impl;
//
//import com.jm.online_store.exception.StockNotFoundException;
//import com.jm.online_store.model.Stock;
//import com.jm.online_store.repository.StockRepository;
//import com.jm.online_store.service.interf.StockService;
//import org.junit.Assert;
//import org.junit.Rule;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
///**
// * класс тестирования для методов StockService.
// */
//class StockServiceImplTest {
//    private final StockRepository stockRepository = Mockito.mock(StockRepository.class);
//    private final StockService stockService = new StockServiceImpl(stockRepository);
//
//    List<Stock> stockList;
//    Stock firstStock;
//    Stock secondStock;
//    Stock thirdStock;
//
//    @BeforeEach
//    void setUp() {
//        stockList = new ArrayList<>();
//        firstStock = Stock.builder()
//                .startDate(LocalDate.now().plusDays(2))
//                .endDate(LocalDate.now().plusDays(12L))
//                .stockTitle("1")
//                .stockText("future")
//                .build();
//        firstStock.setId(1L);
//
//        secondStock = Stock.builder()
//                .startDate(LocalDate.now().minusDays(5L))
//                .endDate(LocalDate.now().plusDays(3L))
//                .stockTitle("2")
//                .stockText("now")
//                .build();
//        secondStock.setId(2L);
//
//        thirdStock = Stock.builder()
//                .startDate(LocalDate.now().minusDays(20L))
//                .endDate(LocalDate.now().minusDays(5L))
//                .stockTitle("3")
//                .stockText("past")
//                .build();
//        thirdStock.setId(3L);
//
//        stockList.add(firstStock);
//        stockList.add(secondStock);
//        stockList.add(thirdStock);
//    }
//
//    @AfterEach
//    void tearDown() {
//        firstStock = null;
//        secondStock = null;
//        thirdStock = null;
//        stockList = null;
//    }
//
//    /**
//     * тест метода поиска всех акций.
//     */
//    @Test
//    void findAll() {
//        when(stockRepository.findAll()).thenReturn(stockList);
//        assertEquals(stockList, stockService.findAll(), "провал теста findAll() - неожидаемый результат");
//    }
//
//    /**
//     * тест метода поиска акции - пробрасывает исключение.
//     */
//    @Test
//    void findAllReturnExeption() {
//        when(stockRepository.findAll()).thenReturn(new ArrayList<>());
//        assertThrows(StockNotFoundException.class, () -> stockService.findAll());
//    }
//
//    /**
//     * тест метода поиска акции по идентификатору.
//     */
//    @Test
//    void findStockByIdThenReturnOneElement() {
//        when(stockRepository.findById(1L)).thenReturn(Optional.ofNullable(firstStock));
//        Stock resultStock = stockService.findStockById(1L);
//        assertEquals(firstStock, resultStock);
//    }
//
//    /**
//     * тест метода поиска акции по идентификатору - пробрасывает исключение.
//     */
//    @Test
//    void findStockByNoIdThenReturnExeption() {
//        when(stockRepository.findById(4L)).thenReturn(Optional.empty());
//        assertThrows(StockNotFoundException.class, () -> stockService.findStockById(4L));
//    }
//
//    /**
//     * тест поиска действующих акций.
//     */
//    @Test
//    void findCurrentStocks() {
//        List<Stock> currentList = new ArrayList<>();
//        currentList.add(secondStock);
//        when(stockRepository
//                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateEquals(LocalDate.now(), LocalDate.now(), null))
//                .thenReturn(currentList);
//        assertEquals(currentList, stockService.findCurrentStocks(), "провал теста findCurrentStocks() - неожидаемый результат");
//    }
//
//    /**
//     * тест поиска действующих акций - проброска исключения при пустом списке.
//     */
//    @Test
//    void findCurrentStocksThrowExeption() {
//        when(stockRepository
//                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateEquals(LocalDate.now(), LocalDate.now(), null))
//                .thenReturn(new ArrayList<>());
//        assertThrows(StockNotFoundException.class, () -> stockService.findCurrentStocks());
//    }
//
//    /**
//     * тест поиска будущих акций.
//     */
//    @Test
//    void findFutureStocks() {
//        List<Stock> futureList = new ArrayList<>();
//        futureList.add(firstStock);
//        when(stockRepository.findByStartDateAfter(LocalDate.now())).thenReturn(futureList);
//        assertEquals(futureList, stockService.findFutureStocks(), "провал теста findFutureStocks() - неожидаемый результат");
//    }
//
//    /**
//     * тест поиска будущих акций - проброска исключения при пустом списке.
//     */
//    @Test()
//    void findFutureStocks_throwExeption() {
//        when(stockRepository.findByStartDateAfter(LocalDate.now())).thenReturn(new ArrayList<>());
//        assertThrows(StockNotFoundException.class, () -> stockService.findFutureStocks());
//    }
//
//    /**
//     * тест поиска прошедших акций.
//     */
//    @Test
//    void findPastStocks() {
//        List<Stock> pastList = new ArrayList<>();
//        pastList.add(thirdStock);
//        when(stockRepository.findByEndDateBefore(LocalDate.now())).thenReturn(pastList);
//        assertEquals(pastList, stockService.findPastStocks(), "провал теста findPastStocks() - неожидаемый результат");
//    }
//
//    /**
//     * тест поиска прошедших акций - проброска исключения при пустом списке.
//     */
//    @Test
//    void findPastStocksThrowExeption() {
//        when(stockRepository.findByEndDateBefore(LocalDate.now())).thenReturn(new ArrayList<>());
//        assertThrows(StockNotFoundException.class, () -> stockService.findPastStocks());
//    }
//
//    /**
//     * тест обновления акции.
//     */
//    @Test
//    void updateStock() {
//        Stock updateStock = new Stock();
//        updateStock.setId(1L);
//        updateStock.setStockText("updateText");
//        updateStock.setStockTitle("updateTitle");
//        updateStock.setStartDate(LocalDate.now().minusDays(2L));
//        updateStock.setEndDate(LocalDate.now().minusDays(1L));
//        updateStock.setStockImg("updateIMG");
//        when(stockRepository.findById(1L)).thenReturn(Optional.ofNullable(firstStock));
//        stockService.updateStock(updateStock);
//        assertEquals(1L, firstStock.getId(), "провал теста updateStock()- ошибка плоля: id");
//        assertEquals("updateText", firstStock.getStockText(), "провал теста updateStock()- ошибка плоля: stockText");
//        assertEquals("updateTitle", firstStock.getStockTitle(), "провал теста updateStock()- ошибка плоля: StockTitle");
//        assertEquals(LocalDate.now().minusDays(2L), firstStock.getStartDate(), "провал теста updateStock()- ошибка плоля: startDate");
//        assertEquals(LocalDate.now().minusDays(1L), firstStock.getEndDate(), "провал теста updateStock()- ошибка плоля: endDate");
//        assertEquals("updateIMG", firstStock.getStockImg(), "провал теста updateStock()- ошибка плоля: stockImg");
//    }
//
//}