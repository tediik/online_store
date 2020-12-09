package com.jm.online_store.service.interf;

import com.jm.online_store.model.SentStock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SentStockService {
    List<SentStock> findAllByInterval(LocalDate begin, LocalDate end);

    SentStock addSentStock(SentStock sentStock);

    Map<LocalDate,Long> getSentStocksMap(LocalDate begin, LocalDate end);
}
