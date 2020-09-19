package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockFilterDto;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.spec.StockSpec;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public List<Stock> findAll() {
        List<Stock> stockList = stockRepository.findAll();
        if (stockList.isEmpty()) {
            throw new StockNotFoundException();
        }
        return stockList;
    }

    /**
     * Метод извлекает страницу акций
     *
     * @param page параметры страницы
     * @return Page<Stock> возвращает страницу новостей
     */
    @Override
    public Page<Stock> findPage(Pageable page, StockFilterDto filterDto) {
        Specification<Stock> spec = StockSpec.get(filterDto);
        Page<Stock> stockPage = stockRepository.findAll(spec, page);
        if (stockPage.isEmpty()) {
            throw new StockNotFoundException();
        }
        return stockPage;
    }

    @Override
    public Stock findStockById(Long id) {
        Optional<Stock> stock = stockRepository.findById(id);
        if (stock.isEmpty()) {
            throw new StockNotFoundException();
        }
        return stock.get();
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
