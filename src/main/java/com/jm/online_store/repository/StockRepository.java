package com.jm.online_store.repository;

import com.jm.online_store.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findById(Long id);

    void deleteStockById(Long id);

    List<Stock> findAllByStartDateBetweenAndEndDateIsAfter(LocalDate beginningOfPeriod, LocalDate endOfPeriod, LocalDate currentDate);
}
