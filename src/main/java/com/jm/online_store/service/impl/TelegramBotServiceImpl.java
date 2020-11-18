package com.jm.online_store.service.impl;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.RepairOrderService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {
    private final StockService stockService;
    private final RepairOrderService repairOrderService;

    @Value("${spring.server.url}")
    private String url;

    @Override
    public String getActualStocks() {
        StringBuilder message = new StringBuilder();
        List<Stock> actualStocks = stockService.findActualStocks();
        if (actualStocks.isEmpty()) {
            return "Акций нет\uD83D\uDE2D";
        }
        for (Stock stock : actualStocks) {
            message.append("\uD83D\uDD25")
                    .append(stock.getStockTitle())
                    .append(".\nПодробности на ")
                    .append(url)
                    .append("/global/stockDetails/")
                    .append(stock.getId())
                    .append("\n\n");
        }
        return message.toString();
    }

    @Override
    public String getRepairOrder(String orderNumber) {
        if (repairOrderService.existsByOrderNumber(orderNumber)) {
            RepairOrder repairOrder = repairOrderService.findByOrderNumber(orderNumber);
            return "Заказа с номером " + repairOrder.getOrderNumber() +
                    transformRepairOrderStatus(repairOrder.getRepairOrderType());
        }
        return "Заказ с номером " + orderNumber + " не найден \uD83D\uDE2D" +
                "\nПожалуйста, проверьте правильность номера вашего заказа";
    }

    @Override
    public String getHelloMessage() {
        return "Привет \u270C " +
                "\nЯ бот магазина online_store: " + url +
                "\nЯ умею рассказывать об акциях проходящих в нашем магазине " +
                "и могу узнать о статусе вашего заказа на ремонт." +
                "\nВведите /help, чтобы узнать, что я умею";
    }

    @Override
    public String getHelpMessage() {
        return "Поддерживаемые команды: " +
                "\n/start - начать общение с ботом" +
                "\n/help - получить справку по командам" +
                "\n/getstocks - узнать о наших акциях " +
                "\n/checkrepair [номер вашего заказа] - узнать статус вашего заказа на ремонт";
    }

    @Override
    public String getDefaultMessage() {
        return "\uD83E\uDD37" +
                "\nВведите /help, чтобы узнать, что я умею";
    }

    public String transformRepairOrderStatus(RepairOrderType orderStatus) {
        switch (orderStatus) {
            case ACCEPTED: {
                return " принят";
            }
            case DIAGNOSTICS: {
                return " находится на диагностике";
            }
            case IN_WORK: {
                return " в ремонте";
            }
            case COMPLETE: {
                return " выполнен";
            }
            case ARCHIVED: {
                return " заархивирован";
            }
            case CANCELED: {
                return " отменен";
            }
            default: {
                return " не найден";
            }
        }
    }
}
