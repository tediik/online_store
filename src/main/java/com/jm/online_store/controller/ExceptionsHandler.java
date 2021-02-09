package com.jm.online_store.controller;

import com.jm.online_store.exception.customer.CustomerServiceException;
import com.jm.online_store.exception.news.NewsServiceException;
import com.jm.online_store.model.dto.ResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(value = { NewsServiceException.class })
    public ResponseEntity<Object> handlerUserServiceException(NewsServiceException ex) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { CustomerServiceException.class })
    public ResponseEntity<Object> handlerCustomerServiceException(CustomerServiceException ex) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
