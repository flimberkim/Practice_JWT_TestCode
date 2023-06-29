package com.example.bolta_justin.global.exception;

import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public class GlobalException extends CustomException {
    private GlobalExceptionType globalExceptionType;

    public GlobalException(GlobalExceptionType globalExceptionType){
        this.globalExceptionType = globalExceptionType;
    }

    @Override
    public CustomExceptionType getExceptionType() {
        return this.globalExceptionType;
    }
}
