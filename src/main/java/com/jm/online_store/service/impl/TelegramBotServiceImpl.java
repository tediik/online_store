package com.jm.online_store.service.impl;

import com.jm.online_store.service.interf.TelegramBotService;
import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.RepairOrderService;
import com.jm.online_store.service.interf.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {
    private final StockService stockService;
    private final RepairOrderService repairOrderService;

    @Override
    public String getActualStocks() {
        StringBuilder message = new StringBuilder();
        List<Stock> actualStocks = stockService.findActualStocks();
        for (Stock stock : actualStocks) {
            message.append("\uD83D\uDD25")
                    .append(stock.getStockTitle())
                    .append(".\nПодробности на http://localhost:9999/global/stockDetails/")
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
                "\nЯ бот магазина online_store: http://localhost:9999/" +
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
                return " завершен";
            }
            default: {
                return " не найден";
            }
        }
    }
}
