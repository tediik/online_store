package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<Stock> findAll() {
        LocalDate presentDate = LocalDate.now();
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            if (((stock.getStartDate()) != null)) {
                if (presentDate.isBefore(stock.getStartDate())) {
                    stock.setStockType(Stock.StockType.FUTURE);
                } else if (stock.getEndDate() != null && presentDate.isAfter(stock.getEndDate())
                        || (presentDate.equals(stock.getEndDate()))) {
                    stock.setStockType(Stock.StockType.PAST);
                } else if ((presentDate.isAfter(stock.getStartDate()) || (presentDate.equals(stock.getStartDate()))
                        && stock.getEndDate() != null && (presentDate.isBefore(stock.getEndDate())))) {
                    stock.setStockType(Stock.StockType.CURRENT);
                } else {
                    stock.setStockType(Stock.StockType.CURRENT);
                }
            }
        }
        return stocks;
    }

    @Override
    public Stock findStockById(Long id) {
        if (stockRepository.findById(id) == null) {
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
