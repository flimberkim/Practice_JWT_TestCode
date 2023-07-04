package com.example.bolta_justin.barcode.service.impl;

import com.example.bolta_justin.Token.repository.TokenRepository;
import com.example.bolta_justin.barcode.dto.BarcodeReqDTO;
import com.example.bolta_justin.barcode.entity.Barcode;
import com.example.bolta_justin.barcode.exception.BarcodeException;
import com.example.bolta_justin.barcode.repository.BarcodeRepository;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtProperties;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.entity.Member;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.member.exception.MemberExceptionType;
import com.example.bolta_justin.member.repository.MemberRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceImplTest {
    @InjectMocks
    BarcodeServiceImpl barcodeServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    BarcodeRepository barcodeRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    TokenRepository tokenRepository;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(barcodeServiceImpl).build();
    }

    @Test
    @DisplayName("바코드 생성(성공)")
    @WithMockUser
    void createBarcode() throws Exception{

        BarcodeReqDTO barcodeReqDTO = BarcodeReqDTO.builder()
                .identifier("1234567890")
                .build();

        when(memberRepository.findByIdentifier(any(Integer.class))).thenReturn(Optional.ofNullable(new Member()));

        ResponseDTO responseDTO = barcodeServiceImpl.createBarcode(barcodeReqDTO);

        assertEquals("바코드 생성 성공", responseDTO.getMessage());
        verify(memberRepository).findByIdentifier(any(Integer.class));

    }

    @Test
    @DisplayName("바코드 생성(실패, 잘못된 회원식별자)")
    @WithMockUser
    void createBarcodeFail() throws Exception{

        BarcodeReqDTO barcodeReqDTO = BarcodeReqDTO.builder()
                .identifier("1234567890")
                .build();


        when(memberRepository.findByIdentifier(any(Integer.class))).thenThrow(new MemberException(MemberExceptionType.MEMBER_IDENTIFIER_NOT_FOUND));

        Assertions.assertThrows(MemberException.class, () -> {
            barcodeServiceImpl.createBarcode(barcodeReqDTO);
        });
        verify(memberRepository).findByIdentifier(any(Integer.class));

    }

    @Test
    @DisplayName("바코드 조회(성공)")
    @WithMockUser
    void getBarcode() throws Exception{

        BarcodeReqDTO barcodeReqDTO = BarcodeReqDTO.builder()
                .identifier("1234567890")
                .build();

        Member findMember = Member.builder()
                .barcode(Barcode.builder()
                        .barcode("1234567890")
                        .build())
                .build();

        when(memberRepository.findByIdentifier(any(Integer.class))).thenReturn(Optional.ofNullable(findMember));
        ResponseDTO responseDTO = barcodeServiceImpl.getBarcode(Integer.valueOf(barcodeReqDTO.getIdentifier()));

        assertEquals("바코드 조회", responseDTO.getMessage());
        verify(memberRepository).findByIdentifier(any(Integer.class));

    }

    @Test
    @DisplayName("바코드 조회(실패, 바코드 없음)")
    @WithMockUser
    void getBarcodeFail() throws Exception{

        BarcodeReqDTO barcodeReqDTO = BarcodeReqDTO.builder()
                .identifier("1234567890")
                .build();

        when(memberRepository.findByIdentifier(any(Integer.class))).thenReturn(Optional.ofNullable(new Member()));

        Assertions.assertThrows(BarcodeException.class, () -> {
            barcodeServiceImpl.getBarcode(Integer.valueOf(barcodeReqDTO.getIdentifier()));
        });
        verify(memberRepository).findByIdentifier(any(Integer.class));

    }

}