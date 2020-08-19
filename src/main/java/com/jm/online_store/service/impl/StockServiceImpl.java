package com.jm.online_store.service.impl;

import com.jm.online_store.model.Stock;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    @Override
    public Optional<Stock> findStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public List<Stock> findAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public void addStock(Stock stock) {
        stockRepository.save(stock);
    }

    @Override
    @Transactional
    public void deleteStockById(Long id) {
        stockRepository.deleteStockById(id);
    }
}
