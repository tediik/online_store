package com.jm.online_store.bot.Telegram.service;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
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
    public String getSomeQuantityOfNews() {
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
                "\nЯ умею рассказывать об акциях проходящих в нашем магазине, " +
                "могу узнать о статусе вашего заказа на ремонт," +
                "\nсообщать вам новости нашего магазина ." +
                "\nВведите /help, чтобы узнать, что я умею";
    }

    @Override
    public String getHelpMessage() {
        return "Поддерживаемые команды: " +
                "\n/start - начать общение с ботом" +
                "\n/help - получить справку по командам" +
                "\n/getstocks - узнать о наших акциях " +
                "\n/checkrepair [номер вашего заказа] - узнать статус вашего заказа на ремонт"+
                "\n/getNews узнать все новости";
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

    @Override
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();

        row1.add(new KeyboardButton("Узнать о наших акциях"));
        row2.add(new KeyboardButton("Узнать статус вашей заявки на ремонт "));
        row3.add(new KeyboardButton("Будьте в курсе событий. Наши новости"));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
