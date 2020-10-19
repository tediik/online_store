package com.jm.online_store.exception;

public class RepairOrderNotFoundException extends RuntimeException {
    public RepairOrderNotFoundException() {
        super("Not found RepairOrder");
    }
}