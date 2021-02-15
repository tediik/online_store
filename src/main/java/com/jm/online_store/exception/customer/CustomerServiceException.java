package com.jm.online_store.exception.customer;

public class CustomerServiceException extends RuntimeException {

    public CustomerServiceException() {
        super("Customer not found");
    }

    public CustomerServiceException(String message) {
        super(message);
    }
}
