package com.jm.online_store.exception;

public class CharacteristicNotFoundException extends RuntimeException {
    public CharacteristicNotFoundException() {
        super("Characteristic Not Found");
    }
}
