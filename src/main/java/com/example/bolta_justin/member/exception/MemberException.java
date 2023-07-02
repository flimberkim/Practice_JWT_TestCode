package com.example.bolta_justin.member.exception;


import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public class MemberException extends CustomException {

    private final MemberExceptionType memberExceptionType;

    public MemberException(MemberExceptionType memberExceptionType) {
        this.memberExceptionType = memberExceptionType;
    }

    @Override
    public CustomExceptionType getExceptionType() {
        return this.memberExceptionType;
    }
}
