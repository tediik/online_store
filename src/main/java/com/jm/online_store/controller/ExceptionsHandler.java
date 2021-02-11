package com.jm.online_store.controller;

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
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class ExceptionsHandler {

    private static HttpStatus detectCodeStatus(String message) {
        String code = message.replaceAll("[^0-9]", "");
        HttpStatus httpStatus;
        switch (code) {
            case ("404"):
                httpStatus = HttpStatus.NOT_FOUND;
                break;
            case ("400"):
                httpStatus = HttpStatus.FORBIDDEN;
                break;
            case ("406") :
                httpStatus = HttpStatus.NOT_ACCEPTABLE;
                break;
            default:
                httpStatus = HttpStatus.BAD_REQUEST;
        }
        return httpStatus;
    }

    @ExceptionHandler(value = { NewsServiceException.class })
    public ResponseEntity<Object> handlerUserServiceException(NewsServiceException ex ) {


        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), detectCodeStatus(ex.getMessage()));
    }

    @ExceptionHandler(value = { CustomerServiceException.class })
    public ResponseEntity<Object> handlerCustomerServiceException(CustomerServiceException ex, HttpStatus httpStatus) {

        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), httpStatus);
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
