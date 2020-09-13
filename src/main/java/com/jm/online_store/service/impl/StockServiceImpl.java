package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public Page<Stock> findAll(Pageable page) {
        return stockRepository.findAll(page);
    }

    @Override
    public Stock findStockById(Long id) {
        if (stockRepository.findById(id).isEmpty()) {
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
    public List<Stock> findCurrentStocks() {
        List<Stock> currentStocks = stockRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateEquals(LocalDate.now(), LocalDate.now(), null);
        if (currentStocks.isEmpty()) {
            throw new StockNotFoundException();
        }
        return currentStocks;
    }

    @Override
    @Transactional
    public void deleteStockById(Long id) {
        stockRepository.deleteStockById(id);
    }

    @Override
    public List<Stock> findFutureStocks() {
        List<Stock> currentStocks = stockRepository.findByStartDateAfter(LocalDate.now());
        if (currentStocks.isEmpty()) {
            throw new StockNotFoundException();
        }
        return currentStocks;
    }

    @Override
    public List<Stock> findPastStocks() {
        List<Stock> currentStocks = stockRepository.findByEndDateBefore(LocalDate.now());
        if (currentStocks.isEmpty()) {
            throw new StockNotFoundException();
        }
        return currentStocks;
    }

    @Override
    public void updateStock(Stock stock) {
        Stock modifiedStock = stockRepository.findById(stock.getId()).orElseThrow(StockNotFoundException::new);
        modifiedStock.setStartDate(stock.getStartDate());
        modifiedStock.setEndDate(stock.getEndDate());
        modifiedStock.setStockTitle(stock.getStockTitle());
        modifiedStock.setStockText(stock.getStockText());
        modifiedStock.setStockImg(stock.getStockImg());
        stockRepository.save(modifiedStock);
    }
}
