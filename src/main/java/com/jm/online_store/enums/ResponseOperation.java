package com.jm.online_store.enums;

public enum ResponseOperation {

    HAS_BEEN_DELETED("%s - has been successfully deleted"),
    HAS_BEEN_SAVED("%s - has been successfully saved"),
    HAS_BEEN_UPDATED("%s - has been successfully updated"),
    NO_ERROR(""),
    HAS_NOT_BEEN_DELETED("%s - has not been deleted");
    private String message;


    ResponseOperation(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
