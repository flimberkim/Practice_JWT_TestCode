package com.example.bolta_justin.point.controller;

import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.barcode.exception.BarcodeExceptionType;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.partner.exception.PartnerException;
import com.example.bolta_justin.partner.exception.PartnerExceptionType;
import com.example.bolta_justin.point.dto.PointAvailableReqDTO;
import com.example.bolta_justin.point.dto.PointListReqDTO;
import com.example.bolta_justin.point.dto.PointListResDTO;
import com.example.bolta_justin.point.dto.PointUseReqDTO;
import com.example.bolta_justin.point.entity.Point;
import com.example.bolta_justin.point.service.PointService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    @InjectMocks
    PointController pointController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    PointService pointService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    JwtUtil jwtUtil;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(pointController).build();
    }

    @Test
    @DisplayName("포인트 적립 및 사용 성공")
    @WithMockUser
    void handlePoint() throws Exception {

        // Given
        PointUseReqDTO pointUseReqDTO = PointUseReqDTO.builder()
                .pointBarcode("1234567890")
                .pointAmount(10)
                .partnerId(1L)
                .pointUseType("적립")
                .build();
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        ResponseDTO resultDTO = new ResponseDTO<>().ok("포인트 적립 성공");

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(pointService.handlePoint(any(PointUseReqDTO.class), any(String.class))).thenReturn(resultDTO);
        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/points")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pointUseReqDTO))
        );

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("포인트 적립 성공"))
                .andDo(print())
                .andReturn();

        verify(pointService).handlePoint(any(PointUseReqDTO.class), any(String.class));

    }

    @Test
    @DisplayName("포인트 적립 및 사용 실패(존재하지 않는 가맹점)")
    @WithMockUser
    void handlePointFail1() throws Exception {

        // Given
        PointUseReqDTO pointUseReqDTO = PointUseReqDTO.builder()
                .pointBarcode("1234567890")
                .pointAmount(10)
                .partnerId(1L)
                .pointUseType("적립")
                .build();
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        ResponseDTO resultDTO = new ResponseDTO<>().ok("포인트 적립 성공");

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(pointService.handlePoint(any(PointUseReqDTO.class), any(String.class))).thenThrow(new PartnerException(PartnerExceptionType.PARTNER_NOT_FOUND));
        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                MockMvcRequestBuilders.post("/points")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pointUseReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof PartnerException);
        PartnerException partnerException = (PartnerException) thrown.getCause();
        assertEquals(PartnerExceptionType.PARTNER_NOT_FOUND, partnerException.getExceptionType());
    }

    @Test
    @DisplayName("포인트 적립 및 사용 실패(존재하지 않는 바코드)")
    @WithMockUser
    void handlePointFail2() throws Exception {

        // Given
        PointUseReqDTO pointUseReqDTO = PointUseReqDTO.builder()
                .pointBarcode("1234567890")
                .pointAmount(10)
                .partnerId(1L)
                .pointUseType("적립")
                .build();
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(pointService.handlePoint(any(PointUseReqDTO.class), any(String.class))).thenThrow(new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));
        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/points")
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                            .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(pointUseReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof BarcodeException);
        BarcodeException barcodeException = (BarcodeException) thrown.getCause();
        assertEquals(BarcodeExceptionType.BARCODE_NOT_FOUND, barcodeException.getExceptionType());
    }

    @Test
    @DisplayName("회원 본인의 포인트 조회(성공)")
    @WithMockUser
    void getMyPoint() throws Exception {
        // Given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        ResponseDTO resultDTO = new ResponseDTO<>().ok("업종별 사용 가능 포인트입니다.");

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(pointService.getMyPoint(any(String.class)))
                .thenReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/points")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("업종별 사용 가능 포인트입니다."))
                .andDo(print())
                .andReturn();

        verify(pointService).getMyPoint("accessToken");

    }

    @Test
    @DisplayName("회원 본인의 포인트 조회(실패, 바코드 없음)")
    @WithMockUser
    void getMyPointFail() throws Exception {
        // Given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(pointService.getMyPoint(any(String.class))).thenThrow(new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.get("/points")
                            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                            .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest));
        });

        // Then
        assertTrue(thrown.getCause() instanceof BarcodeException);
        BarcodeException barcodeException = (BarcodeException) thrown.getCause();
        assertEquals(BarcodeExceptionType.BARCODE_NOT_FOUND, barcodeException.getExceptionType());

    }

    @Test
    @DisplayName("멤버십 바코드의 포인트 조회(성공)")
    @WithMockUser
    void getBarcodePoint() throws Exception {
        // Given
        PointAvailableReqDTO pointAvailableReqDTO = PointAvailableReqDTO.builder()
                .memberBarcode("1234567890")
                .build();

        ResponseDTO resultDTO = new ResponseDTO<>().ok("업종별 사용 가능 포인트입니다.");

        given(pointService.getBarcodePoint(any(String.class)))
                .willReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/points/barcodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(pointAvailableReqDTO))
        );

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("업종별 사용 가능 포인트입니다."))
                .andDo(print())
                .andReturn();

        verify(pointService).getBarcodePoint(any(String.class));

    }

    @Test
    @DisplayName("멤버십 바코드의 포인트 조회(실패, 바코드 없음)")
    @WithMockUser
    void getBarcodePointFail() throws Exception {
        // Given
        PointAvailableReqDTO pointAvailableReqDTO = PointAvailableReqDTO.builder()
                .memberBarcode("1234567890")
                .build();

        when(pointService.getBarcodePoint(any(String.class))).thenThrow(new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/points/barcodes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(pointAvailableReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof BarcodeException);
        BarcodeException barcodeException = (BarcodeException) thrown.getCause();
        assertEquals(BarcodeExceptionType.BARCODE_NOT_FOUND, barcodeException.getExceptionType());

    }

}