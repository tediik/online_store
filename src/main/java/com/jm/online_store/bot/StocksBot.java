package com.jm.online_store.bot;

import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.StockService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@Component
public class StocksBot extends TelegramLongPollingBot {

    private final StockService stockService;

    @Value("${stocks-bot.name}")
    private String botUsername;

    @Value("${stocks-bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = createMessage(update);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage createMessage(Update update) {
        StringBuilder message = new StringBuilder();
        long chatId = update.getMessage().getChatId();

        switch (update.getMessage().getText()) {
            case "/start": {
                message.append("Привет" + "\u270C" +
                        "\nЯ бот магазина online_store: http://localhost:9999" +
                        "\nЯ умею рассказывать об акциях проходящих в нашем магазине." +
                        "\nЕсли хочешь узнать о них, отправь мне команду: /getStocks");
                break;
            }
            case "/getStocks": {
                List<Stock> publishedStocks = stockService.findPublishedStocks();
                for (Stock stock : publishedStocks) {
                    message.append("\uD83D\uDD25" + stock.getStockTitle() +
                            ".\nПодробности на http://localhost:9999/global/stockDetails/" +
                            stock.getId() + "\n\n");
                }
                break;
            }
            default: {
                message.append("Сейчас я умею рассказывать только о наших акциях" + "\uD83D\uDE2D" +
                        "\nЕсли хочешь узнать о них, отправь мне команду: /getStocks");
                break;
            }
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message.toString())
                .build();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
