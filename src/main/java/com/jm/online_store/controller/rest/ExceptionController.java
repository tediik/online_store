package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.InvalidEmailException;
import com.jm.online_store.exception.NewsNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EmailAlreadyExistsException.class, InvalidEmailException.class})
    public ResponseEntity<ResponseDto<Object>> handleExceptions(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseDto<>(false, getBody(ex)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class, NewsNotFoundException.class})
    public ResponseEntity<ResponseDto<Object>> handleNotFoundExceptions(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(new ResponseDto<>(false, getBody(ex)), HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> getBody(Throwable throwable) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", throwable.getMessage());
        return body;
    }
}
