package com.jm.online_store.exception.newsService;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewsNotFoundException extends RuntimeException {

    public NewsNotFoundException(String message) {
        super(message);
    }

}
