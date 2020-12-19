package com.jm.online_store.exception;

public class CommonSettingsNotFoundException extends RuntimeException {
    public CommonSettingsNotFoundException() {
        super("Settings not found");
    }
}
