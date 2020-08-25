package com.jm.online_store.service.interf;

import com.jm.online_store.model.Stock;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface SchedulingService {
    @Scheduled(cron = "${emailStockSending.delay}")
    void sendStocksToCustomers();

    /**
     * Метод создает messageBody для рассылки акций
     *
     * @param currentAndFutureStocks лист актуальных акций
     * @return Строка с текстом.
     */
    default String prepareMessageBody(List<Stock> currentAndFutureStocks) {
        StringBuilder messageForEmail = new StringBuilder();
        for (Stock stock : currentAndFutureStocks) {
            messageForEmail
                    .append(stock.getStockTitle())
                    .append("\n")
                    .append(stock.getStockText())
                    .append("\n")
                    .append("\n")
                    .append("Акция проходит с: ")
                    .append(stock.getStartDate())
                    .append(" по: ")
                    .append(stock.getEndDate())
                    .append("\n")
                    .append("\n");
        }
        return messageForEmail.toString();
    }
}
