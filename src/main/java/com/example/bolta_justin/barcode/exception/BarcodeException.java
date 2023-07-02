package com.example.bolta_justin.barcode.exception;

import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;

public class BarcodeException extends CustomException {

        private BarcodeExceptionType barcodeExceptionType;

        public BarcodeException(BarcodeExceptionType barcodeExceptionType){
            this.barcodeExceptionType = barcodeExceptionType;
        }

        @Override
        public CustomExceptionType getExceptionType() {
            return this.barcodeExceptionType;
        }
}
