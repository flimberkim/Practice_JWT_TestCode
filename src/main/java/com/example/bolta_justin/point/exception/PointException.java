package com.example.bolta_justin.point.exception;

import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public class PointException extends CustomException {

    private PointExceptionType pointExceptionType;

    public PointException(PointExceptionType pointExceptionType){
        this.pointExceptionType = pointExceptionType;
    }

    @Override
    public CustomExceptionType getExceptionType() {
        return this.pointExceptionType;
    }
}
