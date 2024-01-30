package com.example.commonproject.exception;

public class BadRequestException extends RuntimeExceptionBase{
    public BadRequestException(String message) {
        super(message);
    }
}
