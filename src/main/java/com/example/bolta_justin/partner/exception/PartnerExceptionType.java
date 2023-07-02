package com.example.bolta_justin.partner.exception;

import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public enum PartnerExceptionType implements CustomExceptionType {

    PARTNER_NOT_FOUND(404, "존재하지 않는 가맹점입니다.");

    private int errorCode;
    private String errorMessage;

    PartnerExceptionType(int errorCode, String errorMessage){
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
