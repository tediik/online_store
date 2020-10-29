package com.jm.online_store.exception;

public class FeedbackNotFoundException extends RuntimeException {
    public FeedbackNotFoundException() {
        super("Not found Feedback");
    }
}
