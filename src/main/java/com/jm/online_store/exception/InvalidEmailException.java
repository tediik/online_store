package com.jm.online_store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("Email is invalid");
    }
}
