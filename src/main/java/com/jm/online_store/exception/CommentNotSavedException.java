package com.jm.online_store.exception;

public class CommentNotSavedException extends RuntimeException {
    public CommentNotSavedException() {
        super("You do not have adequate permission to perform this operation!");
    }
}
