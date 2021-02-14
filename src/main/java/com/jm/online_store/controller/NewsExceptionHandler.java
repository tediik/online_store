package com.jm.online_store.controller;

import com.jm.online_store.exception.newsService.NewsNotFoundException;
import com.jm.online_store.exception.orderSerivce.OrdersNotFoundException;
import com.jm.online_store.exception.userService.UserNotFoundException;
import com.jm.online_store.model.dto.ResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NewsExceptionHandler {

    @ExceptionHandler(NewsNotFoundException.class)
    public ResponseEntity<Object> handlerNewsNotFoundException(NewsNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handlerUserNotFoundException(UserNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrdersNotFoundException.class)
    public ResponseEntity<Object> handlerOrdersNotFoundException(OrdersNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }
}
