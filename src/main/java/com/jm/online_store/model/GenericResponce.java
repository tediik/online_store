package com.jm.online_store.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenericResponce {

    private String message;
    private String error;

    public GenericResponce(String message){
        this.message = message;
    }
}
