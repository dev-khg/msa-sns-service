package com.example.preorder.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public abstract class RuntimeExceptionBase extends RuntimeException {
    public RuntimeExceptionBase(String message) {
        super(message);
    }
}
