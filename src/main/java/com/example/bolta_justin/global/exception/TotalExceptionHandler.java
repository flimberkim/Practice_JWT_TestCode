package com.example.bolta_justin.global.exception;

import com.example.bolta_justin.global.dto.ErrorDTO;
import com.example.bolta_justin.global.exception.base.CustomException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TotalExceptionHandler {
    @ExceptionHandler(value = GlobalException.class)
    public ErrorDTO handleGlobalException(CustomException ce){
        return ErrorDTO.builder()
                .errorCode(ce.getExceptionType().getErrorCode())
                .errorMessage(ce.getExceptionType().getMessage())
                .build();
    }
}
