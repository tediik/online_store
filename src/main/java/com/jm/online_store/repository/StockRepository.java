package com.jm.online_store.repository;

import com.jm.online_store.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findById(Long id);

    void deleteStockById(Long id);
}
