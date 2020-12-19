package com.jm.online_store.bot.Handler;

import com.jm.online_store.bot.service.MainMenuService;
import com.jm.online_store.bot.service.TelegramBotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

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
     * Метод для обработки команды, поступившей к нашему боту.
     *В двух словах как обрабатывется входящий update и как на него отвечать обхяснить не получится,
     * по этому , если вам выпала таска добавить новую фичу , гугл в помщь :)
     * @param update входящее обновление, содержит в себе команду, которую надо обработать,
     *               id чата из которого поступила команда и множество другой полезной информации
     * @return SendMessage с текстовым ответом на команду
     */
    public BotApiMethod<?> handleCommand(Update update) {

        String command;
        CallbackQuery callbackQuery;

        if (update.hasCallbackQuery()) {
            callbackQuery = update.getCallbackQuery();
            command = callbackQuery.getData();
        } else {
            command = update.getMessage().getText();
        }

        String message;
        String orderNumber = "";

        if (command.startsWith("Y") || command.startsWith("N")) {
            orderNumber = command;
            command = "Проверяем статус вашего заказа";
        }

        switch (command) {
            case "Узнать о наших акциях": {
                message = telegramBotService.getActualStocks();
                break;
            }
            case "Узнать статус вашей заявки на ремонт": {
                message = telegramBotService.askingOrderNumber();
                break;
            }
            case "Проверяем статус вашего заказа": {
                message = telegramBotService.repairOrderStatus(orderNumber);
                break;
            }
            case "Наши новости": {
                message = telegramBotService.getNews();
                break;
            }
            case "Перезапустить бота":
            default: {
                message = telegramBotService.getHelloMessage();
                break;
            }
        }

        return SendMessage.builder().replyMarkup(mainMenuService.getMainMenuKeyboard())
                .chatId(update.getMessage().getChatId().toString())
                .text(message)
                .build();
    }


}
