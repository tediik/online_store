package com.jm.online_store.service.impl;

import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockFilterDto;
import com.jm.online_store.repository.StockRepository;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.spec.StockSpec;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private static final String uploadDirectory = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "images" + File.separator + "stocks";

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
    public List<Stock> findPublishedStocks() {
        List<Stock> publishedStocks = stockRepository.findPublishedStocks();
        if (publishedStocks.isEmpty()) {
            throw new StockNotFoundException();
        }
        return publishedStocks;
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

    /**
     * updateStockImage method receives Stock Id and Multipart Image file
     * saves Image in Uploads/images folder
     * and sets saved image to StockImage
     *
     */

    @Override
    @Transactional
    public String updateStockImage(Long stockId, MultipartFile file) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(StockNotFoundException::new);
        String fileName = StringUtils.cleanPath(UUID.randomUUID() + "." + file.getOriginalFilename());
        String uniqueFilename = fileName.replaceAll("\\s+","");
        log.debug("UpdatingStockImage. fileName: {}" + uniqueFilename);
        if (!file.isEmpty()) {
            Path fileNameAndPath = Paths.get(uploadDirectory, uniqueFilename);
            log.debug("UpdatingStockImage. fileNameandPath: {}", fileNameAndPath);
            try {
                byte[] bytes = file.getBytes();
                Files.write(fileNameAndPath, bytes);
                //Set stock image
                stock.setStockImg(uniqueFilename);
                log.debug("UpdatingStockImage. new StockImg: {}", uniqueFilename);
                stockRepository.save(stock);
                log.debug("File saved successfully: {}", uniqueFilename);
                log.debug("UpdatingStockImage. Stock ID: {}", stock.getId());
                log.debug("UpdatingStockImage. New StockImg: {}", stock.getStockImg());
            } catch (IOException e) {
                log.debug("Failed to store file: {}, because: {}", fileNameAndPath, e.getMessage());
            }
        } else {
            log.debug("Failed to store file - file is not present {}", uniqueFilename);
        }
        String newFilePath = File.separator + "uploads" + File.separator + "images" + File.separator + "stocks" + File.separator + uniqueFilename;
        log.debug("New file full path: {}", newFilePath);
        return newFilePath;
    }

    /**
     * deleteUserImage method receives authorised user's Id
     * deletes current user's profile picture and sets a default avatar
     * default avatar cannot be deleted
     */

    @Override
    @Transactional
    public String deleteStockImage(Long stockId) {
        final String defaultStockImg = StringUtils.cleanPath("default.jpg");
        Stock stock = stockRepository.findById(stockId).orElseThrow(StockNotFoundException::new);
        //Get stockPicture name from Stock and delete this stock picture from Uploads
        if(stock.getStockImg() != null) {
            Path fileNameAndPath = Paths.get(uploadDirectory, stock.getStockImg());
            //Check if deleting picture is not a default avatar
            try {
                if (!fileNameAndPath.getFileName().toString().equals(defaultStockImg)) {
                    Files.delete(fileNameAndPath);
                }
            } catch (IOException e) {
                log.debug("Failed to delete file: {}, because: {} ", fileNameAndPath.getFileName().toString(), e.getMessage());
            }
        }
        //Set a default picture as a stock default Picture
        stock.setStockImg(defaultStockImg);
        return File.separator + "uploads" + File.separator + "images" + File.separator + "stocks" + File.separator + defaultStockImg;
    }

    /**
     * updateStockPublished method receives Stock Id and value of published checkbox
     * and sets published true/false to Stock
     *
     */
    @Override
    @Transactional
    public String updateStockPublished(Long stockId, boolean stockPublishedCheckbox) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(StockNotFoundException::new);
        log.debug("UpdatingStockPublished. StockId = " + stockId + " published: " + stockPublishedCheckbox);
//        stock.setPublished(stockPublishedCheckbox);
//        String publishedStatus = String.valueOf(stockPublishedCheckbox);
//        return publishedStatus;
        return "OK";
    }
}
