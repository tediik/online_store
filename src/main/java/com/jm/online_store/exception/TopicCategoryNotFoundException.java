package com.jm.online_store.exception;

public class TopicCategoryNotFoundException extends RuntimeException{
    public TopicCategoryNotFoundException(String message) {
        super(message);
    }
}
