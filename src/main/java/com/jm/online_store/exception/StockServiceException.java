package com.jm.online_store.exception;

public class StockServiceException extends RuntimeException {
    public StockServiceException(String message) {
        super(message);
    }
}
