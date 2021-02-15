package com.jm.online_store.exception.aatest;

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
    CATEGORY("CATEGORY "), CATEGORIES("CATEGORIES ");
    private final String description;

    ExceptionEnums(String description) {
        this.description = description;
    }

    public String getText() {
        return description;
    }

}

