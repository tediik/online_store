package com.jm.online_store.exception;

public class AddressNotFoundException extends RuntimeException  {

    public AddressNotFoundException(){
        super();
    }

    public AddressNotFoundException(String message) {
        super(message);
    }
}
