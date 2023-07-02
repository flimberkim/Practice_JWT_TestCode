package com.example.bolta_justin.barcode.exception;

import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public enum BarcodeExceptionType implements CustomExceptionType {
    BARCODE_NOT_FOUND(404, "존재하지 않는 바코드입니다. 바코드를 발급받으세요."),
    BARCODE_SHORTAGE(402, "포인트가 부족합니다.");

    private int errorCode;
    private String errorMessage;

    BarcodeExceptionType(int errorCode, String errorMessage){
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
