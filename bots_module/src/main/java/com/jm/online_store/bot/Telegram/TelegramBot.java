package com.jm.online_store.bot.Telegram;


import com.jm.online_store.bot.Telegram.Handler.FundamentalBotCommandHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Телеграм бот
 */
@Slf4j
@RequiredArgsConstructor
@Getter
@Setter
@Component
public class TelegramBot extends TelegramWebhookBot {

    private final FundamentalBotCommandHandler telegramBotCommandHandler;

    @Value("${telegram-bot.name}")
    private String botUsername;

    @Value("${telegram-bot.token}")
    private String botToken;

    @Value("${telegram-bot.webhook-path}")
    private String botPath;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return telegramBotCommandHandler.handleCommand(update);
    }
}
