package com.jm.online_store.bot.Telegram.service;

public interface TelegramBotService {

    String getActualStocks();

    String getRepairOrder(String orderNumber);

    String getHelloMessage();

    String getDefaultMessage();

    String getHelpMessage();
}
