package com.jm.online_store.exception;

public class SharedStockNotFoundException extends RuntimeException {

    public SharedStockNotFoundException(String message) {
        super(message);
    }
}
