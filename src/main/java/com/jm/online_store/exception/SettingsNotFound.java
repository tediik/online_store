package com.jm.online_store.exception;

public class SettingsNotFound extends RuntimeException {
    public SettingsNotFound() {
        super("Settings not found");
    }
}
