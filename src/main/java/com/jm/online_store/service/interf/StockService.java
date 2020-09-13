package com.jm.online_store.service.interf;

import com.jm.online_store.model.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    Stock findStockById(Long id);

    List<Stock> findAllStocks();

    void addStock(Stock stock);

    void deleteStockById(Long id);

    Page<Stock> findAll(Pageable pageable);

    List<Stock> findCurrentStocks();

    List<Stock> findFutureStocks();

    List<Stock> findPastStocks();

    void updateStock(Stock stock);
}
