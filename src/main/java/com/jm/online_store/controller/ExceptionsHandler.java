package com.jm.online_store.controller;

import com.jm.online_store.exception.admin.UserServiceException;
import com.jm.online_store.exception.customer.CustomerServiceException;
import com.jm.online_store.exception.newsService.NewsServiceException;
import com.jm.online_store.exception.orderSerivce.OrderServiceException;
import com.jm.online_store.exception.sentStockService.SentStockServiceException;
import com.jm.online_store.exception.stockService.StockServiceException;
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
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { CustomerServiceException.class })
    public ResponseEntity<Object> handlerCustomerServiceException(CustomerServiceException ex) {

        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { UserServiceException.class })
    public ResponseEntity<Object> handlerUserServiceException(UserServiceException ex) {

        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { SentStockServiceException.class })
    public ResponseEntity<Object> handlerSentStockServiceException(SentStockServiceException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { OrderServiceException.class })
    public ResponseEntity<Object> handlerOrderServiceException(OrderServiceException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = { StockServiceException.class })
    public ResponseEntity<Object> handlerStockServiceException(StockServiceException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
