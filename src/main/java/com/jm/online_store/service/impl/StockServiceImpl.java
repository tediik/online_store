package com.jm.online_store.service.impl;

import com.jm.online_store.exception.aatest.ExceptionConstants;
import com.jm.online_store.exception.aatest.ExceptionEnums;
import com.jm.online_store.exception.stockService.StockExceptionConstants;
import com.jm.online_store.exception.stockService.StockNotFoundException;
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
        return stockRepository.findAll();
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
            throw new StockNotFoundException(ExceptionEnums.STOCK_PAGE.getText() + ExceptionConstants.NOT_FOUND);
        }
        return stockPage;
    }

    @Override
    public Stock findStockById(Long id) {
        return  stockRepository.findById(id).orElseThrow(() -> new StockNotFoundException(ExceptionEnums.STOCK.getText() +
                String.format( ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));
    }

    @Override
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public List<Stock> findCurrentStocks() {
        return stockRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateEquals(LocalDate.now(),
                LocalDate.now(), null);
    }

    @Override
    @Transactional
    public void deleteStockById(Long id) {
        stockRepository.findById(id).orElseThrow(() -> new StockNotFoundException(String.format(ExceptionEnums.STOCK.getText() +
                        ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));
        stockRepository.deleteStockById(id);
    }

    @Override
    public List<Stock> findFutureStocks() {
        return stockRepository.findByStartDateAfter(LocalDate.now());
    }

    @Override
    public List<Stock> findPastStocks() {
        return stockRepository.findByEndDateBefore(LocalDate.now());
    }

    /**
     * Метод извлекает список акций, отмеченных для публикации на главной странице
     * @return publishedStocks возвращает список опубликованных акций со значением true в поле published из БД
     */
    @Override
    public List<Stock> findPublishedStocks() {
        return stockRepository.findPublishedStocks();
    }

    @Override
    public List<Stock> findActualStocks() {
        return stockRepository.findAllByPublishedIsTrueAndEndDateAfterOrEndDateEquals(LocalDate.now(), LocalDate.now());
    }

    @Override
    public Stock updateStock(Stock stock) {
        Stock modifiedStock = stockRepository.findById(stock.getId()).orElseThrow(() ->
                        new StockNotFoundException(ExceptionEnums.STOCK.getText() + ExceptionConstants.NOT_FOUND));
        modifiedStock.setStartDate(stock.getStartDate());
        modifiedStock.setEndDate(stock.getEndDate());
        modifiedStock.setStockTitle(stock.getStockTitle());
        modifiedStock.setStockText(stock.getStockText());
        modifiedStock.setStockImg(stock.getStockImg());
        modifiedStock.setPublished(stock.isPublished());
        return stockRepository.save(modifiedStock);
    }
}




