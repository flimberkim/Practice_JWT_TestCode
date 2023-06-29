package com.example.bolta_justin.global.exception.base;

public abstract class CustomException extends RuntimeException{
    public abstract CustomExceptionType getExceptionType();
}
