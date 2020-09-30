package com.jm.online_store.exception;

public class ProductsNotFoundException extends RuntimeException{
    public ProductsNotFoundException(String message) {
        super(message);
    }
}
