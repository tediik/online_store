package com.jm.online_store.bot.telegram.service;

import com.jm.online_store.enums.RepairOrderType;
import com.jm.online_store.model.News;
import com.jm.online_store.model.RepairOrder;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.RepairOrderService;
import com.jm.online_store.service.interf.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {

    private final StockService stockService;

    private final RepairOrderService repairOrderService;

    private final NewsService newsService;

    @Value("${spring.server.url}")
    private String url;

    @Override
    public String getNews() {
        StringBuilder massage = new StringBuilder();
        List<News> newsList = newsService.getAllPublished();
        newsList.forEach(news ->
                massage.append("\uD83E\uDD4B")
                        .append(news.getTitle())
                        .append(".\nПодробности на ")
                        .append(url)
                        .append("/news/")
                        .append(news.getId())
                        .append("\n\n"));

        return massage.toString();
    }

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
    public String askingOrderNumber() {
        return "Введите пожалуйста номер заявки на ремонт." +
                "\nОна начинается с N . . .(семь цифр) или Y . . . (семь цифр)";
    }

    @Override
    public String repairOrderStatus(String orderNumber) {
        if (repairOrderService.existsByOrderNumber(orderNumber)) {
            RepairOrder repairOrder = repairOrderService.findByOrderNumber(orderNumber);
            return "Заказ с номером заявки " + repairOrder.getOrderNumber() +
                    transformRepairOrderStatus(repairOrder.getRepairOrderType());
        }
        return "Заказ с номером " + orderNumber + " не найден \uD83D\uDE2D" +
                "\nПожалуйста, проверьте правильность номера вашего заказа";
    }

    @Override
    public String getHelloMessage() {
        return "Привет \u270C " +
                "\nЯ бот магазина online_store: " + url +
                "\nЯ умею рассказывать об акциях проходящих в нашем магазине, " +
                "могу узнать о статусе вашего заказа на ремонт," +
                "\nсообщать вам новости нашего магазина ." ;
    }


    public String transformRepairOrderStatus(RepairOrderType orderStatus) {
        switch (orderStatus) {
            case ACCEPTED: {
                return" находится в статусе ПРИНЯТ";
            }
            case DIAGNOSTICS: {
                return " находится в статусе НА ДИАГНО́СТИКЕ";
            }
            case IN_WORK: {
                return " находится в статусе В РЕМОНТЕ";
            }
            case COMPLETE: {
                return " находится в статусе ВЫПОЛНЕН";
            }
            case ARCHIVED: {
                return " находится в статусе ЗААРХИВИРОВАН";
            }
            case CANCELED: {
                return " находится в статусе ОТМЕНЕН";
            }
            default: {
                return "НЕ НАЙДЕН";
            }
        }
    }

}
