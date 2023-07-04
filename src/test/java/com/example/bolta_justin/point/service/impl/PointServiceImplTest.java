package com.example.bolta_justin.point.service.impl;


import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.barcode.exception.BarcodeExceptionType;
import com.example.bolta_justin.barcode.repository.BarcodeRepository;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtProperties;
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
import com.example.bolta_justin.point.dto.PointListReqDTO;
import com.example.bolta_justin.point.dto.PointListResDTO;
import com.example.bolta_justin.point.dto.PointUseReqDTO;
import com.example.bolta_justin.point.entity.Point;
import com.example.bolta_justin.point.enums.UseType;
import com.example.bolta_justin.point.exception.PointException;
import com.example.bolta_justin.point.repository.PointRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    @InjectMocks
    PointServiceImpl pointServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    PointRepository pointRepository;

    @Mock
    PartnerRepository partnerRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BarcodeRepository barcodeRepository;

    @Mock
    MemberService memberService;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    UseType useType;

    @Mock
    PartnerType partnerType;

    @Mock
    Pageable pageable;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(pointServiceImpl).build();
    }

    @Test
    @DisplayName("포인트 사용/적립 성공")
    @WithMockUser
    void handlePoint() throws Exception{

        PointUseReqDTO pointUseReqDTO = PointUseReqDTO.builder()
                .pointAmount(10)
                .pointBarcode("1234567890")
                .pointUseType("적립")
                .partnerId(1L)
                .build();

        String accessToken = "accessToken";

        Partner tempPartner = Partner.builder()
                .partnerType(PartnerType.A)
                .id(1L)
                .name("test")
                .build();

        Barcode tempBarcode = Barcode.builder()
                .barcode("1234567890")
                .aPoint(10)
                .bPoint(10)
                .cPoint(10)
                .build();

        when(memberService.formatNumber(jwtUtil.getIdentifier(accessToken))).thenReturn("000000001");
        when(partnerRepository.findById(pointUseReqDTO.getPartnerId())).thenReturn(Optional.ofNullable(tempPartner));
        when(barcodeRepository.findByBarcode(pointUseReqDTO.getPointBarcode())).thenReturn(Optional.ofNullable(tempBarcode));

        ResponseDTO responseDTO = pointServiceImpl.handlePoint(pointUseReqDTO, accessToken);
        System.out.println(responseDTO.getStateCode());
        assertEquals("200", String.valueOf(responseDTO.getStateCode()));

    }

    @Test
    @DisplayName("포인트 사용/적립 실패(존재하지 않는 가맹점)")
    @WithMockUser
    void handlePointFail1() throws Exception{

        PointUseReqDTO pointUseReqDTO = PointUseReqDTO.builder()
                .pointAmount(10)
                .pointBarcode("1234567890")
                .pointUseType("적립")
                .partnerId(1L)
                .build();

        when(partnerRepository.findById(pointUseReqDTO.getPartnerId())).thenThrow(new PartnerException(PartnerExceptionType.PARTNER_NOT_FOUND));
        Assertions.assertThrows(PartnerException.class, () -> {
            partnerRepository.findById(pointUseReqDTO.getPartnerId());
        });

    }

    @Test
    @DisplayName("포인트 사용/적립 실패(존재하지 않는 바코드)")
    @WithMockUser
    void handlePointFail2() throws Exception{

        PointUseReqDTO pointUseReqDTO = PointUseReqDTO.builder()
                .pointAmount(10)
                .pointBarcode("1234567890")
                .pointUseType("적립")
                .partnerId(1L)
                .build();

        when(barcodeRepository.findByBarcode(pointUseReqDTO.getPointBarcode())).thenThrow(new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));
        Assertions.assertThrows(BarcodeException.class, () -> {
            barcodeRepository.findByBarcode(pointUseReqDTO.getPointBarcode());
        });

    }

    @Test
    @DisplayName("바코드에 포인트 적용하는 메서드(성공)")
    @WithMockUser
    void applyToBarcode() throws Exception{

        Barcode tempBarcode = Barcode.builder()
                .barcode("1234567890")
                .aPoint(10)
                .bPoint(10)
                .cPoint(10)
                .build();

        Barcode resultBarcode = pointServiceImpl.applyToBarcode(tempBarcode, PartnerType.A, "적립", 10);

        assertEquals(20, resultBarcode.getAPoint());

    }

    @Test
    @DisplayName("바코드에 포인트 적용하는 메서드(실패, 포인트 부족)")
    @WithMockUser
    void applyToBarcodeFail() throws Exception{

        Barcode tempBarcode = Barcode.builder()
                .barcode("1234567890")
                .aPoint(10)
                .bPoint(10)
                .cPoint(10)
                .build();

        Assertions.assertThrows(BarcodeException.class, () -> {
            pointServiceImpl.applyToBarcode(tempBarcode, PartnerType.A, "사용", 100);
        });

    }

    @Test
    @DisplayName("내 포인트 조회(성공)")
    @WithMockUser
    void getMyPoint() throws Exception{

        Barcode tempBarcode = Barcode.builder()
                .barcode("1234567890")
                .aPoint(10)
                .bPoint(10)
                .cPoint(10)
                .build();

        Member tempMember = Member.builder()
                .barcode(tempBarcode)
                .build();

        String accessToken = "accessToken";

        when(jwtUtil.getIdentifier(accessToken)).thenReturn(1);
        when(memberRepository.findByIdentifier(any(Integer.class))).thenReturn(Optional.ofNullable(tempMember));

       assertEquals("업종별 사용 가능 포인트입니다.", pointServiceImpl.getMyPoint(accessToken).getMessage());

    }

    @Test
    @DisplayName("내 포인트 조회(실패, 존재하지 않는 회원)")
    @WithMockUser
    void getMyPointFail1() throws Exception{

        String accessToken = "accessToken";

        when(memberRepository.findByIdentifier(1)).thenThrow(new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Assertions.assertThrows(MemberException.class, () -> {
            memberRepository.findByIdentifier(1);
        });

    }

    @Test
    @DisplayName("내 포인트 조회(바코드가 존재하지 않음)")
    @WithMockUser
    void getMyPointFail2() throws Exception{

        String accessToken = "accessToken";

        when(jwtUtil.getIdentifier(accessToken)).thenReturn(1);
        when(memberRepository.findByIdentifier(1)).thenReturn(Optional.of(new Member()));
        Assertions.assertThrows(BarcodeException.class, () -> {
            pointServiceImpl.getMyPoint(accessToken);
        });

    }

    @Test
    @DisplayName("바코드의 포인트 조회(성공)")
    @WithMockUser
    void getBarcodePoint() throws Exception{
        String barcode = "1234567890";

        Barcode tempBarcode = Barcode.builder()
                .barcode("1234567890")
                .aPoint(10)
                .bPoint(10)
                .cPoint(10)
                .build();

        when(barcodeRepository.findByBarcode(barcode)).thenReturn(Optional.ofNullable(tempBarcode));

        assertEquals("업종별 사용 가능 포인트입니다.", pointServiceImpl.getBarcodePoint(barcode).getMessage());

    }

    @Test
    @DisplayName("바코드의 포인트 조회(실패, 없는 바코드)")
    @WithMockUser
    void getBarcodePointFail() throws Exception{
        String barcode = "1234567890";

        when(barcodeRepository.findByBarcode(barcode)).thenReturn(Optional.ofNullable(null));
        Assertions.assertThrows(BarcodeException.class, () -> {
            pointServiceImpl.getBarcodePoint(barcode);
        });

    }

    @Test
    @DisplayName("포인트 사용 리스트 조회(성공)")
    @WithMockUser
    void getPointList() throws Exception{

        String startDate = "2023-01-01 00:00:00";
        String endDate = "2023-12-31 23:59:59";
        String barcodeValue = "1234567890";
        Barcode barcode = Barcode.builder().barcode(barcodeValue).build();

        PointListReqDTO pointListReqDTO = PointListReqDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .barcode(barcodeValue)
                .build();

        List<Point> pointList = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Point> pointPage = new PageImpl<>(pointList, pageable, pointList.size());

        when(barcodeRepository.findByBarcode(barcodeValue)).thenReturn(Optional.of(barcode));
        when(pointRepository.findAllByPointDateBetweenAndBarcode(any(LocalDateTime.class), any(LocalDateTime.class), eq(barcode), eq(pageable))).thenReturn(pointPage);


        Page<PointListResDTO> resultPage = pointServiceImpl.getPointList(pointListReqDTO, pageable);


        assertEquals(pointPage.map(PointListResDTO::new), resultPage);

    }

    @Test
    @DisplayName("포인트 사용 리스트 조회(실패, 바코드 없음)")
    @WithMockUser
    void getPointListFail1() throws Exception{

        String startDate = "2023-01-01 00:00:00";
        String endDate = "2023-12-31 23:59:59";
        String barcodeValue = "1234567890";

        PointListReqDTO pointListReqDTO = PointListReqDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .barcode(barcodeValue)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(barcodeRepository.findByBarcode(barcodeValue)).thenThrow(new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));

        Assertions.assertThrows(BarcodeException.class, () -> {
            pointServiceImpl.getPointList(pointListReqDTO, pageable);
        });

    }

    @Test
    @DisplayName("포인트 사용 리스트 조회(실패, 조회기간 형식 오류)")
    @WithMockUser
    void getPointListFail2() throws Exception{

        String startDate = "2023-01-01 000:00";
        String endDate = "2023-12-31 23:59:59";
        String barcodeValue = "1234567890";

        PointListReqDTO pointListReqDTO = PointListReqDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .barcode(barcodeValue)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(barcodeRepository.findByBarcode(barcodeValue)).thenReturn(Optional.of(new Barcode()));

        Assertions.assertThrows(PointException.class, () -> {
            pointServiceImpl.getPointList(pointListReqDTO, pageable);
        });

    }

    @Test
    @DisplayName("포인트 사용 리스트 조회(실패, 조회기간 순서 오류)")
    @WithMockUser
    void getPointListFail3() throws Exception{

        String startDate = "2024-01-01 00:00:00";
        String endDate = "2023-12-31 23:59:59";
        String barcodeValue = "1234567890";

        PointListReqDTO pointListReqDTO = PointListReqDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .barcode(barcodeValue)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        when(barcodeRepository.findByBarcode(barcodeValue)).thenReturn(Optional.of(new Barcode()));

        Assertions.assertThrows(PointException.class, () -> {
            pointServiceImpl.getPointList(pointListReqDTO, pageable);
        });

    }

}