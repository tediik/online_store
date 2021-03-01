package com.jm.online_store.exception;

public class TopicAlreadyExists extends RuntimeException{
    public TopicAlreadyExists(String message) {
        super(message);
    }
}
