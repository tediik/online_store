package com.jm.online_store.service.impl;

import com.jm.online_store.repository.StockRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/**
 * Модульное, функциональное тестирование (JUnit5 + Mockito)
 */
class SchedulingServiceImplTest {
    private StockRepository stockRepository;
    private String dayOfWeek;

    @BeforeAll
    void beforeTest(){

    }

    @Test
    void sendStocksToCustomers() {
    given()
        stockRepository.findAllByStartDateBetweenAndEndDateIsAfter( );
    }


}