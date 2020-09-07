package com.jm.online_store.service.interf;

import com.jm.online_store.model.Stock;

import java.util.List;
import java.util.Optional;

public interface StockService {

    Stock findStockById(Long id);

    List<Stock> findAllStocks();

    void addStock(Stock stock);

    void deleteStockById(Long id);

    List<Stock> findAll();

    List<Stock> findCurrentStocks();

    List<Stock> findFutureStocks();

    List<Stock> findPastStocks();

    void updateStock(Stock stock);
}
