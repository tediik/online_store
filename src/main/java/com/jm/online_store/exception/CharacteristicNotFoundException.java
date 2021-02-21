package com.jm.online_store.exception;

public class CharacteristicNotFoundException extends RuntimeException {

    public CharacteristicNotFoundException() {
    }

    public CharacteristicNotFoundException(String message) {
        super(message);
    }
}
