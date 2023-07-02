package com.example.bolta_justin.point.service.impl;

import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.barcode.repository.BarcodeRepository;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.entity.Member;
import com.example.bolta_justin.member.repository.MemberRepository;
import com.example.bolta_justin.member.service.MemberService;
import com.example.bolta_justin.partner.entity.Partner;
import com.example.bolta_justin.partner.enums.PartnerType;
import com.example.bolta_justin.partner.repository.PartnerRepository;
import com.example.bolta_justin.point.dto.*;
import com.example.bolta_justin.point.entity.Point;
import com.example.bolta_justin.point.enums.UseType;
import com.example.bolta_justin.point.repository.PointRepository;
import com.example.bolta_justin.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        Partner findPartner = partnerRepository.findById(partnerId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
        Barcode findBarcode = barcodeRepository.findByBarcode(pointBarcode).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 바코드입니다."));

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
                if(resultBarcode.getAPoint() + pointToSum < 0) throw new IllegalArgumentException("포인트가 부족합니다.");
                resultBarcode.setAPoint(resultBarcode.getAPoint() + pointToSum);
                break;
            case B:
                if(resultBarcode.getBPoint() + pointToSum < 0) throw new IllegalArgumentException("포인트가 부족합니다.");
                resultBarcode.setBPoint(resultBarcode.getBPoint() + pointToSum);
                break;
            case C:
                if(resultBarcode.getCPoint() + pointToSum < 0) throw new IllegalArgumentException("포인트가 부족합니다.");
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
        Member findMember = memberRepository.findByIdentifier(identifier).orElseThrow();
        Barcode findBarcode = findMember.getBarcode();

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
        Barcode findBarcode = barcodeRepository.findByBarcode(barcode).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 바코드입니다."));

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return pointRepository.findAllByPointDateBetween(LocalDateTime.parse(startDate, formatter), LocalDateTime.parse(endDate, formatter), pageable)
                .map(PointListResDTO::new);
    }
}
