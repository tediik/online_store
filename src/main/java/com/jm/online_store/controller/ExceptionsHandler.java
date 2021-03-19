package com.jm.online_store.controller;

import com.jm.online_store.exception.AddressAlreadyExists;
import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.AlreadyExists;
import com.jm.online_store.exception.CategoriesNotFoundException;
import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.EmployeeNotFoundException;
import com.jm.online_store.exception.NewsNotFoundException;
import com.jm.online_store.exception.OrdersNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.exception.TopicAlreadyExists;
import com.jm.online_store.exception.TopicCategoryAlreadyExists;
import com.jm.online_store.exception.TopicCategoryNotFoundException;
import com.jm.online_store.exception.TopicNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.exception.UserServiceException;
import com.jm.online_store.model.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class ExceptionsHandler {


    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> handlerEmployeeNotFoundException(EmployeeNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()),  HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExists.class)
    public ResponseEntity<Object> handlerAlreadyExistsException(AlreadyExists ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handlerProductNotFoundException(ProductNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CharacteristicNotFoundException.class)
    public ResponseEntity<Object> handlerCharacteristicNotFoundException(CharacteristicNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Object> handlerCategoriesNotFoundException(CustomerNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriesNotFoundException.class)
    public ResponseEntity<Object> handlerCategoriesNotFoundException(CategoriesNotFoundException ex ) {
        return new ResponseEntity<>(new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { UserServiceException.class })
    public ResponseEntity<Object> handlerUserServiceException(UserServiceException ex) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TopicCategoryAlreadyExists.class)
    public ResponseEntity<Object> handlerTopicCategoryAlreadyExistsException(TopicCategoryAlreadyExists ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressAlreadyExists.class)
    public ResponseEntity<Object> handlerAddressAlreadyExistsException(AddressAlreadyExists ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TopicCategoryNotFoundException.class)
    public ResponseEntity<Object> handlerTopicCategoryNotFoundException(TopicCategoryNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TopicAlreadyExists.class)
    public ResponseEntity<Object> handlerTopicAlreadyExistsException(TopicAlreadyExists ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TopicNotFoundException.class)
    public ResponseEntity<Object> handlerTopicNotFoundException(TopicNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NewsNotFoundException.class)
    public ResponseEntity<Object> handlerNewsNotFoundException(NewsNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handlerUserNotFoundException(UserNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<?> handleAddressNotFoundException(AddressNotFoundException ex) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrdersNotFoundException.class)
    public ResponseEntity<Object> handlerOrdersNotFoundException(OrdersNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<Object> handlerStockNotFoundException(StockNotFoundException ex ) {
        return new ResponseEntity<>
                (new ResponseDto<>(false, ex.getMessage()), HttpStatus.NOT_FOUND);
    }
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException() {
        return new ResponseEntity<>
                (new ResponseDto<>(false,  "Превышен допустимый размер файла - " + maxFileSize +"."), HttpStatus.PAYLOAD_TOO_LARGE);
    }

}
