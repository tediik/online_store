package com.jm.online_store.controller;

public enum ResponseOperation {

    NEWS_HAS_BEEN_DELETED("the news has been successfully deleted"),
    NEWS_HAS_BEEN_SAVED("the news has been successfully saved"),
    NEWS_HAS_BEEN_UPDATED("the news has been successfully updated");


    private String message;


    ResponseOperation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
