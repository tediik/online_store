package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.exception.stockService.StockExceptionConstants;
import com.jm.online_store.exception.stockService.StockServiceException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    private static final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "images" +
            File.separator + "stocks";

    @Override
    public String updateStockImage(MultipartFile file) {
        String uniqueFilename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path copyLocation = Paths
                    .get(uploadDir, uniqueFilename);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  File.separator + "uploads" + File.separator + "images" + File.separator + "stocks" + File.separator + uniqueFilename;
    }

    @Override
    public List<Stock> findAll() {
        List<Stock> stockList = stockRepository.findAll();
        if (stockList.isEmpty()) {
            throw new StockServiceException(StockExceptionConstants.NOT_FOUND_STOCKS);
        }
        return stockList;
    }

    /**
     * Метод извлекает страницу акций
     * @param page параметры страницы
     * @return Page<Stock> возвращает страницу акций
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
            throw new StockServiceException(StockExceptionConstants.NOT_FOUND_STOCK);
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

    /**
     * Метод извлекает список акций, отмеченных для публикации на главной странице
     * @return publishedStocks возвращает список опубликованных акций со значением true в поле published из БД
     */
    @Override
    public List<Stock> findPublishedStocks() {
        List<Stock> publishedStocks = stockRepository.findPublishedStocks();
        if (publishedStocks.isEmpty()) {
            throw new StockNotFoundException();
        }
        return publishedStocks;
    }

    @Override
    public List<Stock> findActualStocks() {
        return stockRepository.findAllByPublishedIsTrueAndEndDateAfterOrEndDateEquals(LocalDate.now(), LocalDate.now());
    }

    @Override
    public void updateStock(Stock stock) {
        Stock modifiedStock = stockRepository.findById(stock.getId()).orElseThrow(StockNotFoundException::new);
        modifiedStock.setStartDate(stock.getStartDate());
        modifiedStock.setEndDate(stock.getEndDate());
        modifiedStock.setStockTitle(stock.getStockTitle());
        modifiedStock.setStockText(stock.getStockText());
        modifiedStock.setStockImg(stock.getStockImg());
        modifiedStock.setPublished(stock.isPublished());
        stockRepository.save(modifiedStock);
    }
}
