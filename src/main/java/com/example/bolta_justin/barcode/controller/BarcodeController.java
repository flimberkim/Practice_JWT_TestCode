package com.example.bolta_justin.barcode.controller;

import com.example.bolta_justin.barcode.dto.BarcodeReqDTO;
import com.example.bolta_justin.barcode.service.BarcodeService;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class BarcodeController {
    private final BarcodeService barcodeService;
    private final JwtUtil jwtUtil;
    /**
     * 바코드 생성 요청
     */
    @PostMapping("/barcodes")
    public ResponseDTO createBarcode(@RequestBody BarcodeReqDTO barcodeReqDTO){
        return barcodeService.createBarcode(barcodeReqDTO);
    }

    /**
     * 바코드 조회
     */
    @GetMapping("/barcodes")
    public ResponseDTO getBarcode(HttpServletRequest request){
        String accessToken = jwtUtil.parseHeader(request, HttpHeaders.AUTHORIZATION);
        Integer identifier = jwtUtil.getIdentifier(accessToken);
        return barcodeService.getBarcode(identifier);
    }
}
