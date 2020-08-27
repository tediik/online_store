package com.jm.online_store.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("Email is invalid");
    }
}
