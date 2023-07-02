package com.example.bolta_justin.partner.exception;

import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public class PartnerException extends CustomException {
    private PartnerExceptionType partnerExceptionType;
    
    public PartnerException(PartnerExceptionType partnerExceptionType){
        this.partnerExceptionType = partnerExceptionType;
    }
    
    @Override
    public CustomExceptionType getExceptionType() {
        return this.partnerExceptionType;
    }

}
