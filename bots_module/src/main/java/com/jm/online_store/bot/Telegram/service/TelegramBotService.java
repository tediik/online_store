package com.jm.online_store.bot.Telegram.service;

/**
 * The interface Telegram bot service.
 */
public interface TelegramBotService {

    /**
     * Gets some quantity of news.
     *
     * @return the some quantity of news
     */
    String getNews();

    /**
     * Gets actual stocks.
     *
     * @return the actual stocks
     */
    String getActualStocks();

    /**
     * Gets repair order.
     *
     * @param orderNumber the order number
     * @return the repair order
     */
    String repairOrderStatus(String orderNumber);

    /**
     * Asking order number string.
     *
     * @return the string
     */
    String askingOrderNumber();

    /**
     * Gets hello message.
     *
     * @return the hello message
     */
    String getHelloMessage();

}
