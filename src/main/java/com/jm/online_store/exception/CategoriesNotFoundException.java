package com.jm.online_store.exception;

public class CategoriesNotFoundException extends RuntimeException {

    public CategoriesNotFoundException() {
    }

    public CategoriesNotFoundException(String message) {
        super(message);
    }
}
