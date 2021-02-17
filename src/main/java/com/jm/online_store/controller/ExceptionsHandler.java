package com.jm.online_store.controller;

import com.jm.online_store.exception.CategoriesNotFoundException;
import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.customer.CustomerNotFoundException;
import com.jm.online_store.exception.newsService.NewsNotFoundException;
import com.jm.online_store.exception.orderSerivce.OrdersNotFoundException;
import com.jm.online_store.exception.stockService.StockNotFoundException;
import com.jm.online_store.exception.topicService.TopicAlreadyExists;
import com.jm.online_store.exception.topicService.TopicNotFoundException;
import com.jm.online_store.exception.topicsCategoryService.TopicCategoryAlreadyExists;
import com.jm.online_store.exception.topicsCategoryService.TopicCategoryNotFoundException;
import com.jm.online_store.exception.userService.UserNotFoundException;

import com.jm.online_store.exception.UserServiceException;
import com.jm.online_store.model.dto.ResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler {


    @ExceptionHandler(CharacteristicNotFoundException.class)
    public ResponseEntity<Object> handlerCharacteristicNotFoundException(CharacteristicNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Object> handlerCategoriesNotFoundException(CustomerNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CategoriesNotFoundException.class)
    public ResponseEntity<Object> handlerCategoriesNotFoundException(CategoriesNotFoundException ex ) {
        return new ResponseEntity<>(new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);


    }

    @ExceptionHandler(value = { UserServiceException.class })
    public ResponseEntity<Object> handlerUserServiceException(UserServiceException ex) {

        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(TopicCategoryAlreadyExists.class)
    public ResponseEntity<Object> handlerTopicCategoryAlreadyExistsException(TopicCategoryAlreadyExists ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TopicCategoryNotFoundException.class)
    public ResponseEntity<Object> handlerTopicCategoryNotFoundException(TopicCategoryNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(TopicAlreadyExists.class)
    public ResponseEntity<Object> handlerTopicAlreadyExistsException(TopicAlreadyExists ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TopicNotFoundException.class)
    public ResponseEntity<Object> handlerTopicNotFoundException(TopicNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

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
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<Object> handlerStockNotFoundException(StockNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


}
