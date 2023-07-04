package com.example.bolta_justin.barcode.controller;

import com.example.bolta_justin.barcode.dto.BarcodeReqDTO;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.barcode.exception.BarcodeExceptionType;
import com.example.bolta_justin.barcode.service.BarcodeService;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtFilter;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.member.exception.MemberExceptionType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BarcodeControllerTest {

    @InjectMocks
    BarcodeController barcodeController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    BarcodeService barcodeService;


    @Mock
    JwtUtil jwtUtil;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(barcodeController).build();
    }

    @Test
    @DisplayName("바코드 생성 요청(성공)")
    @WithMockUser
    void createBarcode() throws Exception{
        // Given
        BarcodeReqDTO barcodeReqDTO = new BarcodeReqDTO("1234567890");
        ResponseDTO resultDTO = new ResponseDTO<>().ok("바코드 생성 성공");

        when(barcodeService.createBarcode(any(BarcodeReqDTO.class)))
                .thenReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/barcodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(barcodeReqDTO))
        );

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("바코드 생성 성공"))
                .andDo(print())
                .andReturn();

        verify(barcodeService).createBarcode(any(BarcodeReqDTO.class));
    }

    @Test
    @DisplayName("바코드 생성 요청(잘못된 회원식별자)")
    @WithMockUser
    void createBarcodeFail() throws Exception{
        // Given
        BarcodeReqDTO barcodeReqDTO = new BarcodeReqDTO("1234567890");

        when(barcodeService.createBarcode(any(BarcodeReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_IDENTIFIER_NOT_FOUND));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/barcodes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(barcodeReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_IDENTIFIER_NOT_FOUND, memberException.getExceptionType());

    }

    @Test
    @DisplayName("바코드 조회(성공)")
    @WithMockUser
    void getBarcode() throws Exception{
        // Given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        ResponseDTO resultDTO = new ResponseDTO<>().ok("바코드 조회");

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(jwtUtil.getIdentifier(any(String.class)))
                .thenReturn(Integer.valueOf("00000001"));
        when(barcodeService.getBarcode(any(Integer.class)))
                .thenReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/barcodes")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("바코드 조회"))
                .andDo(print())
                .andReturn();

        verify(barcodeService).getBarcode(any(Integer.class));

    }

    @Test
    @DisplayName("바코드 조회(실패, 존재하지 않는 바코드)")
    @WithMockUser
    void getBarcodeFail() throws Exception{
        // Given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);


        // When
        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(jwtUtil.getIdentifier(any(String.class)))
                .thenReturn(Integer.valueOf("00000001"));
        when(barcodeService.getBarcode(any(Integer.class)))
                .thenThrow(new BarcodeException(BarcodeExceptionType.BARCODE_NOT_FOUND));


        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/barcodes")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest));
        });

        // Then
        assertTrue(thrown.getCause() instanceof BarcodeException);
        BarcodeException barcodeException = (BarcodeException) thrown.getCause();
        assertEquals(BarcodeExceptionType.BARCODE_NOT_FOUND, barcodeException.getExceptionType());

    }
}