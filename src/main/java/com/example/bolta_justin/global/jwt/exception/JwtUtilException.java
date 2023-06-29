package com.example.bolta_justin.global.jwt.exception;

public class JwtUtilException extends RuntimeException{

    private JwtUtilExceptionType jwtUtilExceptionType;

    public JwtUtilException(JwtUtilExceptionType jwtUtilExceptionType){
        this.jwtUtilExceptionType = jwtUtilExceptionType;
    }

    public JwtUtilExceptionType getExceptionType() {
        return jwtUtilExceptionType;
    }
}
