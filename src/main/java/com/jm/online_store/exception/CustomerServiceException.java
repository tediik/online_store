package com.jm.online_store.exception;

public class CustomerServiceException extends RuntimeException {

    public CustomerServiceException() {
    }

    public CustomerServiceException(String message) {
        super(message);
    }

}
