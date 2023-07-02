package com.example.bolta_justin.point.service;

import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.partner.enums.PartnerType;
import com.example.bolta_justin.point.dto.PointListReqDTO;
import com.example.bolta_justin.point.dto.PointListResDTO;
import com.example.bolta_justin.point.dto.PointUseReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {
    ResponseDTO handlePoint(PointUseReqDTO pointUseReqDTO, String accessToken);

    Barcode applyToBarcode(Barcode barcode, PartnerType partnerType, String pointUseType, int pointAmount);

    int getTypePoint(PartnerType partnerType, Barcode barcode);

    ResponseDTO getMyPoint(String accessToken);

    ResponseDTO getBarcodePoint(String barcode);

    Page<PointListResDTO> getPointList(PointListReqDTO pointListReqDTO, Pageable pageable);
}
