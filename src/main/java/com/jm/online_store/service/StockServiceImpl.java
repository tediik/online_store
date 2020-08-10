package com.jm.online_store.service;

import com.jm.online_store.model.Stock;
import com.jm.online_store.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockServiceImpl implements StockService{

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    @Override
    public Optional<Stock> findStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    public List<Stock> findAllStocks() {
        return stockRepository.findAll();
    }

//    @Override
//    public void addStock() {
//        stockRepository.addStock();
//    }

    @Override
    public void deleteStockById(Long id) {
        stockRepository.deleteStockById(id);
    }

//    @Override
//    public void updateStock() {
//        stockRepository.updateStock();
//    }
}
