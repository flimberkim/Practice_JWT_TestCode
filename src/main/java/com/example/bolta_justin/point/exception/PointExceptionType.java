package com.example.bolta_justin.point.exception;

import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public enum PointExceptionType implements CustomExceptionType {

    POINT_INVALID_PERIOD_TYPE(400, "포인트 사용 기간은 yyyy-MM-dd HH:mm:ss 형식으로 입력해주세요."),
    POINT_INVALID_DATE_ORDER(400, "시작일은 종료일보다 이전의 날짜여야 합니다.");
    private int errorCode;
    private String errorMessage;

    PointExceptionType(int errorCode, String errorMessage){
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
