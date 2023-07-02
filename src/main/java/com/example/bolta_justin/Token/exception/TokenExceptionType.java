package com.example.bolta_justin.Token.exception;

import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public enum TokenExceptionType implements CustomExceptionType {
    TOKEN_INVALID(401, "유효하지 않은 토큰입니다.");

    private int errorCode;
    private String errorMessage;

    TokenExceptionType(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }
}
