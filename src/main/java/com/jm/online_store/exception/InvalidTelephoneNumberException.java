package com.jm.online_store.exception;

public class InvalidTelephoneNumberException extends RuntimeException {
    public InvalidTelephoneNumberException() {
        super("Некорректный номер телефона.");
    }
}