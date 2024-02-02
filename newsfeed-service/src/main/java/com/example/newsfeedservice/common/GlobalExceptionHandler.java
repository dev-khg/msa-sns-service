package com.example.newsfeedservice.common;

import com.example.commonproject.exception.BadRequestException;
import com.example.commonproject.exception.InternalServerException;
import com.example.commonproject.exception.UnAuthorizedException;
import com.example.commonproject.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.commonproject.response.ApiResponse.failure;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException e) {
        return badRequest().body(failure(e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerException(InternalServerException e) {
        return internalServerError().body(failure(e.getMessage()));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Void>> handleUnAuthorizedException(UnAuthorizedException e) {
        return new ResponseEntity<>(failure(e.getMessage()), UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime error occurred!", e);
        return internalServerError().body(failure("Sorry! Server has problem."));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleCheckException(Exception e) {
        log.error("Check error occurred!", e);
        return internalServerError().body(failure("Sorry! Server has problem."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handle(MethodArgumentNotValidException e) {
        String defaultMessage = e.getAllErrors().get(0).getDefaultMessage();
        return badRequest().body(failure(defaultMessage));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handle(HttpMessageNotReadableException e) {
        return badRequest().body(failure("please input correct data."));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handle(HttpMediaTypeNotSupportedException e) {
        return badRequest().body(failure("not supported content-type."));
    }
}
