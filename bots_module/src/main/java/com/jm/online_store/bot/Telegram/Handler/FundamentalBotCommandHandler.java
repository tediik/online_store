package com.jm.online_store.bot.Telegram.Handler;

import com.jm.online_store.bot.Telegram.service.MainMenuService;
import com.jm.online_store.bot.Telegram.service.TelegramBotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Обработчик команд для телеграм бота
 */
@Component
@AllArgsConstructor
@Slf4j
public class FundamentalBotCommandHandler {

    private final TelegramBotService telegramBotService;

    private final MainMenuService mainMenuService;

    /**
     * Метод для обработки команды, поступившей к нашему боту
     *
     * @param update входящее обновление, содержит в себе команду, которую надо обработать,
     *               id чата из которого поступила команда и множество другой полезной информации
     * @return SendMessage с текстовым ответом на команду
     */
    public BotApiMethod<?> handleCommand(Update update) {
        String command = update.getMessage().getText();
        String message;
        String orderNumber = "";

        if (command.contains("/checkrepair")) {
            LinkedList<String> s = new LinkedList<>(Arrays.asList(update.getMessage().getText().split(" ")));
            orderNumber = s.getLast();
            command = "/checkrepair";
        } else if (command.contains("/getNews")) {

            command = "/getNews";
        }

        switch (command) {
            case "/start": {
                message = telegramBotService.getHelloMessage();
                break;
            }
            case "/getstocks": {
                message = telegramBotService.getActualStocks();
                break;
            }
            case "/checkrepair": {
                message = telegramBotService.repairOrderStatus(orderNumber);
                break;
            }
            case "/getNews": {
                message = telegramBotService.getNews();
                break;
            }

            default: {
                message = telegramBotService.getDefaultMessage();
                break;
            }
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(message)
                .build();
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {

        Long chatId = buttonQuery.getMessage().getChatId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Здравствуйте \uFE0F" +
                "\nВоспользуйтесь главным меню.");

        if (buttonQuery.getData().equals("Узнать о наших акциях")) {
            callBackAnswer = new SendMessage(chatId.toString(), telegramBotService.getActualStocks());
        } else if (buttonQuery.getData().equals("Будьте в курсе событий. Наши новости")) {
            callBackAnswer = new SendMessage(chatId.toString(), telegramBotService.getNews());
        } else if (buttonQuery.getData().equals("Перезапустить бота \u1F4a")) {
            callBackAnswer = new SendMessage(chatId.toString(), telegramBotService.getHelloMessage());
        } else if (buttonQuery.getData().equals("Узнать статус вашей заявки на ремонт")) {
            callBackAnswer = new SendMessage(chatId.toString(), telegramBotService.askingOrderNumber());
        }
        return callBackAnswer;
    }


}
