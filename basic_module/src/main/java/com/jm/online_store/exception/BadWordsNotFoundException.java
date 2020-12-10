package com.jm.online_store.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BadWordsNotFoundException extends RuntimeException {
    public BadWordsNotFoundException(String message) {
        super("Bad word no found " + message);
    }
}
