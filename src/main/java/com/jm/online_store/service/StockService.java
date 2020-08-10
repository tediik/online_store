package com.jm.online_store.service;

import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockService {

    Optional<Stock> findStockById(Long id);

    List<Stock> findAllStocks();

//    void addStock();

    void deleteStockById(Long id);

//    void updateStock();



}
