package com.jm.online_store.exception;

public class SentStockNotFoundException extends RuntimeException {
    public SentStockNotFoundException() {
        super("SentStock not found");
    }
}
