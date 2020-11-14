package com.jm.online_store.controller.rest;

import com.jm.online_store.bot.TelegramBot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Рест контроллер для телеграм бота
 */
@Slf4j
@RestController()
@AllArgsConstructor
public class TelegramBotRestController {
    private final TelegramBot telegramBot;

    @RequestMapping(value = "/telegram-bot", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        log.debug("Запрос к телеграм боту");
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
