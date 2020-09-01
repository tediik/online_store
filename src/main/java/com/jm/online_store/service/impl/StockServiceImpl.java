package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    @Override
    public Stock findStockById(Long id) {
        if (stockRepository.findById(id).isEmpty()){
            throw new StockNotFoundException();
        }
        return stockRepository.findById(id).get();
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
