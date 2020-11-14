package com.jm.online_store.bot.handler;

import com.jm.online_store.service.interf.TelegramBotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Обработчик команд для телеграм бота
 */
@Component
@AllArgsConstructor
public class TelegramBotCommandHandler {
    private final TelegramBotService telegramBotService;

    /**
     * Метод для обработки команды, поступившей к нашему боту
     *
     * @param update содержит в себе команду, которую надо обработать,
     *               id чата из которого поступила команда и множество другой полезной информации
     * @return SendMessage с ответом на команду
     */
    public SendMessage handleCommand(Update update) {
        String command = update.getMessage().getText();
        String message;
        String orderNumber = "";

        if (command.contains("/checkrepair")) {
            LinkedList<String> s = new LinkedList<>(Arrays.asList(update.getMessage().getText().split(" ")));
            orderNumber = s.getLast();
            command = "/checkrepair";
        }

        switch (command) {
            case "/start": {
                message = telegramBotService.getHelloMessage();
                break;
            }
            case "/help": {
                message = telegramBotService.getHelpMessage();
                break;
            }
            case "/getstocks": {
                message = telegramBotService.getActualStocks();
                break;
            }
            case "/checkrepair": {
                message = telegramBotService.getRepairOrder(orderNumber);
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
}
