package com.example.bolta_justin.global.exception;

import com.example.bolta_justin.Token.exception.TokenException;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.global.dto.ErrorDTO;
import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.partner.exception.PartnerException;
import com.example.bolta_justin.point.exception.PointException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TotalExceptionHandler {
    @ExceptionHandler(value = BarcodeException.class)
    public ErrorDTO handleBarcodeException(CustomException ce){
        return ErrorDTO.builder()
                .errorCode(ce.getExceptionType().getErrorCode())
                .errorMessage(ce.getExceptionType().getMessage())
                .build();
    }

    @ExceptionHandler(value = MemberException.class)
    public ErrorDTO handleMemberException(CustomException ce){
        return ErrorDTO.builder()
                .errorCode(ce.getExceptionType().getErrorCode())
                .errorMessage(ce.getExceptionType().getMessage())
                .build();
    }

    @ExceptionHandler(value = PartnerException.class)
    public ErrorDTO handlePartnerException(CustomException ce){
        return ErrorDTO.builder()
                .errorCode(ce.getExceptionType().getErrorCode())
                .errorMessage(ce.getExceptionType().getMessage())
                .build();
    }

    @ExceptionHandler(value = PointException.class)
    public ErrorDTO handlePointException(CustomException ce){
        return ErrorDTO.builder()
                .errorCode(ce.getExceptionType().getErrorCode())
                .errorMessage(ce.getExceptionType().getMessage())
                .build();
    }

    @ExceptionHandler(value = TokenException.class)
    public ErrorDTO handleTokenException(CustomException ce){
        return ErrorDTO.builder()
                .errorCode(ce.getExceptionType().getErrorCode())
                .errorMessage(ce.getExceptionType().getMessage())
                .build();
    }

}
