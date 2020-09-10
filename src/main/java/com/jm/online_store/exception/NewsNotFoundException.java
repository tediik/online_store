package com.jm.online_store.exception;

public class NewsNotFoundException extends RuntimeException {
    public NewsNotFoundException() {
        super("News not found");
    }
}
