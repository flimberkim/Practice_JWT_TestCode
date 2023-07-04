package com.example.bolta_justin.member.exception;

import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public enum MemberExceptionType implements CustomExceptionType {

    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다. 회원가입을 진행하세요."),
    MEMBER_IDENTIFIER_NOT_FOUND(404, "존재하지 않는 식별번호 입니다."),
    MEMBER_INFORMATION_REQUIRED(400, "회원정보(이메일, 비밀번호, 이름, 연락처)를 모두 입력해주세요."),
    MEMBER_ALREADY_EXISTS(409, "이미 존재하는 회원입니다."),
    MEMBER_EMAIL_FORM(400, "이메일 형식이 올바르지 않습니다."),
    MEMBER_PASSWORD_FORM(400, "비밀번호 형식이 올바르지 않습니다. 5자리 이상 문자와 숫자를 포함해주세요."),
    MEMBER_CONTACT_FORM(400, "연락처는 000-0000-0000 형식으로 입력해주세요."),
    MEMBER_INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    ;

    private final int errorCode;
    private final String errorMessage;

    MemberExceptionType(int errorCode, String errorMessage) {
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
