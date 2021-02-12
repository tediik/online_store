package com.jm.online_store.controller;

import com.jm.online_store.exception.newsService.NewsNotFoundException;
import com.jm.online_store.model.dto.ResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NewsExceptionHandler {

    @ExceptionHandler(value = NewsNotFoundException.class)
    public ResponseEntity<Object> handlerNewsException(NewsNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
