package com.example.preorder.global.advice;

import com.example.preorder.common.ApiResponse;
import com.example.preorder.common.exception.RuntimeExceptionBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.example.preorder.common.ApiResponse.*;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(RuntimeExceptionBase.class)
    public ResponseEntity<ApiResponse<Void>> handle(RuntimeExceptionBase e) {
        return badRequest().body(failure(e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handle(MethodArgumentNotValidException e) {
        String defaultMessage = e.getAllErrors().get(0).getDefaultMessage();
        return badRequest().body(failure(defaultMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
