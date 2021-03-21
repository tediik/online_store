package com.jm.online_store.exception;

public class AddressAlreadyExists extends RuntimeException{
    public AddressAlreadyExists(String message) {
        super(message);
    }
}
