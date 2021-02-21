package com.jm.online_store.enums;

import lombok.Getter;

/**
 * Набор перечислений для использования при обработке исключений.
 * Представляют собой наименования сущностей проекта в ед. и мн. числе
 * Передаются в конструктор и используются в комбинации с константами класса
 * ExceptionConstants
 */

public enum ExceptionEnums {
    USER("USER "), USERS("USERS "),
    CUSTOMER("CUSTOMER "), CUSTOMERS("CUSTOMERS "),
    ORDER("ORDER "), ORDERS("ORDERS"),
    SHARER_STOCK("SHARED STOCK "), SHARER_STOCKS("SHARED STOCKS "),
    STOCK("STOCK "), STOCKS("STOCKS"),
    SENT_STOCK("SENT STOCK "), SENT_STOCKS("SENT STOCKS "),
    STOCK_PAGE("STOCK PAGE "), STOCK_PAGES("STOCK PAGES "),
    NEWS("NEWS "),
    PUBLISHED_NEWS("PUBLISHED NEWS "),
    UNPUBLISHED_NEWS("UNPUBLISHED NEWS "),
    ARCHIVED_NEWS("ARCHIVED NEWS "),
    TOPIC("TOPIC "), TOPICS("TOPICS "),
    TOPIC_CATEGORY("TOPIC CATEGORY "), TOPIC_CATEGORIES("TOPIC CATEGORIES "),
    CATEGORY("CATEGORY "), CATEGORIES("CATEGORIES "),
    CHARACTERISTIC("CHARACTERISTIC "), CHARACTERISTICS("CHARACTERISTICS "),
    EMAIL("EMAIL "),
    ROLE("ROLE "), ROLES("ROLES "),
    PASSWORD("PASSWORD ");

    private final String description;

    ExceptionEnums(String description) {
        this.description = description;
    }

    public String getText() {
        return description;
    }

}
