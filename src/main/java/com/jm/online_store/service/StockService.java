package com.jm.online_store.service;

import com.jm.online_store.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockService {

    Optional<Stock> findStockById(Long id);

    List<Stock> findAllStocks();

    void addStock(Stock stock);

    void deleteStockById(Long id);

    List<Stock> findAll();

}
