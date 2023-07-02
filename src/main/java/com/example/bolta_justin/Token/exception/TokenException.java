package com.example.bolta_justin.Token.exception;

import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public class TokenException extends CustomException {
    
        private TokenExceptionType tokenExceptionType;

        public TokenException(TokenExceptionType tokenExceptionType){
            this.tokenExceptionType = tokenExceptionType;
        }

        @Override
        public CustomExceptionType getExceptionType() {
            return this.tokenExceptionType;
        }

}
