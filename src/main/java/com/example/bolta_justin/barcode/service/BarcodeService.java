package com.example.bolta_justin.barcode.service;

import com.example.bolta_justin.barcode.dto.BarcodeReqDTO;
import com.example.bolta_justin.global.dto.ResponseDTO;

public interface BarcodeService {

    String barcodeGenerator();

    ResponseDTO createBarcode(BarcodeReqDTO barcodeReqDTO);

    ResponseDTO getBarcode(Integer identifier);
}
