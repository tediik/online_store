package com.jm.online_store.service.impl;

import com.jm.online_store.exception.SentStockNotFoundException;
import com.jm.online_store.exception.userService.UserNotFoundException;
import com.jm.online_store.exception.sentStockService.SentStockServiceException;
import com.jm.online_store.exception.sentStockService.SentStocksExceptionConstants;
import com.jm.online_store.model.SentStock;
import com.jm.online_store.repository.SentStockRepository;
import com.jm.online_store.service.interf.SentStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * Сервис класс, имплементация интерфейса {@link SentStockService}
 * Содержит бизнес логику, использует методы репозитория
 * {@link SentStockRepository
 * @link UserService
 * @link StockService
 * }
 */
@Service
@AllArgsConstructor
public class SentStockServiceImpl implements SentStockService {
    private final StockService stockService;
    private final UserService userService;
    private final SentStockRepository sentStockRepository;

    /**
     * Метод находит отправленные акции в заданном интервале
     * @param begin начало интервала
     * @param end  конец интервала
     * @return List<SentStock>
     * @throws SentStockNotFoundException если не найдена ни одна акция в заданном интервале
     */
    @Override
    public List<SentStock> findAllByInterval(LocalDate begin, LocalDate end) {

        return sentStockRepository.findAllBySentDateAfterAndSentDateBefore(
                begin.minusDays(1L),
                end.plusDays(1L));
    }
    /**
     * Метод добавления отправленной акции
     * @param sentStock отправленная акция
     * @return SentStock
     */
    @Override
    @Transactional
    public SentStock addSentStock(SentStock sentStock) {
        SentStock sentStockToAdd = SentStock.builder()
                .stock(stockService.findStockById(sentStock.getStock().getId()))
                .user(userService.findById(sentStock.getUser().getId()).orElseThrow(UserNotFoundException::new))
                .sentDate(sentStock.getSentDate())
                .build();
        return sentStockRepository.save(sentStockToAdd);
    }
    /**
     * Метод строит словарь, словарь где ключом является дата,
     * а значением частота повторений
     * @param begin начало интервала
     * @param end  конец интервала
     * @return Map<LocalDate,Long>
     */
    @Override
    public Map<LocalDate, Long> getSentStocksMap(LocalDate begin, LocalDate end) {
        List<SentStock> sentStocks = findAllByInterval(begin, end);
        Map<LocalDate,Long> result = new HashMap<>();
        for (SentStock sentStock : sentStocks) {
            if(result.keySet().contains(sentStock.getSentDate())) {
                result.put(sentStock.getSentDate(), result.get(sentStock.getSentDate()) + 1);
            } else {
                result.put(sentStock.getSentDate(), 1L);
            }
        }
        return new TreeMap<LocalDate, Long>(result);
    }
}
