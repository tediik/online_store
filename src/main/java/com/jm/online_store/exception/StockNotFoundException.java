package com.jm.online_store.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException() {
        super("Stock not found");
    }
}
