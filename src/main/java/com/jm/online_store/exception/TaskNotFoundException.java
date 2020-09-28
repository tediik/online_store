package com.jm.online_store.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Task not found");
    }
}
