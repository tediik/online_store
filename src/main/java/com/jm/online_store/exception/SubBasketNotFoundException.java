package com.jm.online_store.exception;

public class SubBasketNotFoundException extends RuntimeException {
    public SubBasketNotFoundException() {
        super("SubBasket not found");
    }
}
