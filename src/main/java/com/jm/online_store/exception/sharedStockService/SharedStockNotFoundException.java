package com.jm.online_store.exception.sharedStockService;

public class SharedStockNotFoundException extends RuntimeException {

    public SharedStockNotFoundException(String message) {
        super(message);
    }
}
