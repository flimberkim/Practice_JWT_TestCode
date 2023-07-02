package com.example.bolta_justin.point.service.impl;

import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.barcode.exception.BarcodeExceptionType;
import com.example.bolta_justin.barcode.repository.BarcodeRepository;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.entity.Member;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.member.exception.MemberExceptionType;
import com.example.bolta_justin.member.repository.MemberRepository;
import com.example.bolta_justin.member.service.MemberService;
import com.example.bolta_justin.partner.entity.Partner;
import com.example.bolta_justin.partner.enums.PartnerType;
import com.example.bolta_justin.partner.exception.PartnerException;
import com.example.bolta_justin.partner.exception.PartnerExceptionType;
import com.example.bolta_justin.partner.repository.PartnerRepository;
import com.example.bolta_justin.point.dto.*;
import com.example.bolta_justin.point.entity.Point;
import com.example.bolta_justin.point.enums.UseType;
import com.example.bolta_justin.point.exception.PointException;
import com.example.bolta_justin.point.exception.PointExceptionType;
import com.example.bolta_justin.point.repository.PointRepository;
import com.example.bolta_justin.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointRepository pointRepository;
    private final BarcodeRepository barcodeRepository;
    private final PartnerRepository partnerRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseDTO handlePoint(PointUseReqDTO pointUseReqDTO, String accessToken) {
        String pointUseType = pointUseReqDTO.getPointUseType();
        Long partnerId = pointUseReqDTO.getPartnerId();
        String pointBarcode = pointUseReqDTO.getPointBarcode();
        String identifier = memberService.formatNumber(jwtUtil.getIdentifier(accessToken));

        int pointAmount = pointUseReqDTO.getPointAmount();

        Partner findPartner = partnerRepository.findById(partnerId).orElseThrow(() -> new PartnerException(PartnerExceptionType.PARTNER_NOT_FOUND));
        Barcode findBarcode = barcodeRepository.findByBarcode(pointBarcode).orElseThrow(() -> new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));

        findBarcode = applyToBarcode(findBarcode, findPartner.getPartnerType(), pointUseType, pointAmount);

        Point point = Point.builder()
                .barcode(findBarcode)
                .partner(findPartner)
                .useType(UseType.fromValue(pointUseType))
                .pointDate(LocalDateTime.now())
                .userIdentifier(Integer.valueOf(identifier))
                .currentPoint(getTypePoint(findPartner.getPartnerType(), findBarcode))
                .build();

        pointRepository.save(point);

        return ResponseDTO.builder()
                .stateCode(200)
                .success(true)
                .message("포인트 " + pointUseType + " 성공")
                .data(PointUseResDTO.builder()
                        .barcode(pointBarcode)
                        .pointUseType(pointUseType)
                        .partnerType(findPartner.getPartnerType().getPartnerType())
                        .partnerName(findPartner.getName())
                        .pointUseDate(point.getPointDate())
                        .userIdentifier(identifier)
                        .currentPoint(point.getCurrentPoint())
                        .build())
                .build();
    }

    @Override
    public Barcode applyToBarcode(Barcode barcode, PartnerType partnerType, String pointUseType, int pointAmount) {
        Barcode resultBarcode = barcode;
        int pointToSum = 0;

        if(pointUseType.equals("사용")) {
            pointToSum = -pointAmount;
        } else if(pointUseType.equals("적립")) {
            pointToSum = pointAmount;
        }

        switch (partnerType){
            case A:
                if(resultBarcode.getAPoint() + pointToSum < 0) throw new BarcodeException(BarcodeExceptionType.BARCODE_SHORTAGE);
                resultBarcode.setAPoint(resultBarcode.getAPoint() + pointToSum);
                break;
            case B:
                if(resultBarcode.getBPoint() + pointToSum < 0) throw new BarcodeException(BarcodeExceptionType.BARCODE_SHORTAGE);
                resultBarcode.setBPoint(resultBarcode.getBPoint() + pointToSum);
                break;
            case C:
                if(resultBarcode.getCPoint() + pointToSum < 0) throw new BarcodeException(BarcodeExceptionType.BARCODE_SHORTAGE);
                resultBarcode.setCPoint(resultBarcode.getCPoint() + pointToSum);
                break;
        }

        barcodeRepository.save(resultBarcode);

        return resultBarcode;
    }

    @Override
    public int getTypePoint(PartnerType partnerType, Barcode barcode) {
        switch (partnerType){
            case A:
                return barcode.getAPoint();
            case B:
                return barcode.getBPoint();
            case C:
                return barcode.getCPoint();
        }
        return 0;
    }

    @Override
    public ResponseDTO getMyPoint(String accessToken) {
        Integer identifier = jwtUtil.getIdentifier(accessToken);
        Member findMember = memberRepository.findByIdentifier(identifier).orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
        Barcode findBarcode = findMember.getBarcode();

        if(findBarcode == null) throw new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND);

        return ResponseDTO.builder()
                .stateCode(200)
                .success(true)
                .message("업종별 사용 가능 포인트입니다.")
                .data(PointAvailableResDTO.builder()
                        .barcode(findBarcode.getBarcode())
                        .aPoint("식품 : " + findBarcode.getAPoint())
                        .bPoint("화장품 : " + findBarcode.getBPoint())
                        .cPoint("식당 : " + findBarcode.getCPoint())
                        .build())
                .build();
    }

    @Override
    public ResponseDTO getBarcodePoint(String barcode) {
        Barcode findBarcode = barcodeRepository.findByBarcode(barcode).orElseThrow(() -> new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));

        return ResponseDTO.builder()
                .stateCode(200)
                .success(true)
                .message("업종별 사용 가능 포인트입니다.")
                .data(PointAvailableResDTO.builder()
                        .barcode(findBarcode.getBarcode())
                        .aPoint("식품 : " + findBarcode.getAPoint())
                        .bPoint("화장품 : " + findBarcode.getBPoint())
                        .cPoint("식당 : " + findBarcode.getCPoint())
                        .build())
                .build();
    }

    @Override
    public Page<PointListResDTO> getPointList(PointListReqDTO pointListReqDTO, Pageable pageable) {
        String startDate = pointListReqDTO.getStartDate();
        String endDate = pointListReqDTO.getEndDate();
        String DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        Barcode barcode = barcodeRepository.findByBarcode(pointListReqDTO.getBarcode()).orElseThrow(() -> new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));
        if(!Pattern.matches(DATE_TIME_PATTERN, startDate) || !Pattern.matches(DATE_TIME_PATTERN, endDate)){
            throw new PointException(PointExceptionType.POINT_INVALID_PERIOD_TYPE);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(LocalDateTime.parse(startDate, formatter).isAfter(LocalDateTime.parse(endDate, formatter))) throw new PointException(PointExceptionType.POINT_INVALID_DATE_ORDER);

        return pointRepository.findAllByPointDateBetweenAndBarcode(LocalDateTime.parse(startDate, formatter), LocalDateTime.parse(endDate, formatter), barcode, pageable)
                .map(PointListResDTO::new);
    }
}
