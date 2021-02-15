package com.jm.online_store.exception.topicService;

public class TopicAlreadyExists extends RuntimeException{
    public TopicAlreadyExists(String message) {
        super(message);
    }
}
