package com.jm.online_store.exception;

public class CategoriesNotFoundException extends RuntimeException {
    public CategoriesNotFoundException() {
        super("Categories Not Found");
    }
}
