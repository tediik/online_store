package com.jm.online_store.exception.customer;

import org.springframework.http.HttpStatus;

public class CustomerServiceException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;


    public CustomerServiceException(String message) {
        super(message);
    }

    public CustomerServiceException(String message , HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
