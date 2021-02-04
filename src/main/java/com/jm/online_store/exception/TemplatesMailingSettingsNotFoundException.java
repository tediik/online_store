package com.jm.online_store.exception;

public class TemplatesMailingSettingsNotFoundException extends RuntimeException {
    public TemplatesMailingSettingsNotFoundException() {
        super("Settings not found");
    }
}
