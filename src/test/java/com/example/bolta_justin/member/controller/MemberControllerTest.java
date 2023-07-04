package com.example.bolta_justin.member.controller;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.exception.base.CustomException;
import com.example.bolta_justin.global.exception.base.CustomExceptionType;
import com.example.bolta_justin.global.jwt.JwtFilter;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.global.jwt.exception.JwtExceptionFilter;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LogoutReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.member.exception.MemberExceptionType;
import com.example.bolta_justin.member.service.MemberService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    MemberController memberController;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    MemberService memberService;

    @Mock
    JwtUtil jwtUtil;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }


    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void memberSignup() throws Exception {
        // Given
        SignupReqDTO signupReqDTO = new SignupReqDTO("test@test.com", "hihi123", "test", "000-0000-0000");
        ResponseDTO resultDTO = new ResponseDTO<>().ok("회원가입 성공");

        given(memberService.memberSignup(any(SignupReqDTO.class)))
                .willReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupReqDTO))
        );

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andDo(print())
                .andReturn();

        verify(memberService).memberSignup(any(SignupReqDTO.class));
    }

    @Test
    @DisplayName("회원가입 실패(정보 미 기입)")
    @WithMockUser
    void memberSignupFail1() throws Exception{
        // Given
        SignupReqDTO signupReqDTO = new SignupReqDTO("test@test.com", "", "test", "000-0000-0000");

        when(memberService.memberSignup(any(SignupReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_INFORMATION_REQUIRED));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                MockMvcRequestBuilders.post("/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signupReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_INFORMATION_REQUIRED, memberException.getExceptionType());
    }

    @Test
    @DisplayName("회원가입 실패(이미 존재하는 메일)")
    @WithMockUser
    void memberSignupFail2() throws Exception {
        // Given
        SignupReqDTO signupReqDTO = new SignupReqDTO("test@test.com", "hihi123", "test", "000-0000-0000");

        when(memberService.memberSignup(any(SignupReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_ALREADY_EXISTS));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/member/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(signupReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_ALREADY_EXISTS, memberException.getExceptionType());
    }

    @Test
    @DisplayName("회원가입 실패(메일 형식 오류)")
    @WithMockUser
    void memberSignupFail3() throws Exception{
        // Given
        SignupReqDTO signupReqDTO = new SignupReqDTO("tesest.com", "hihi123", "test", "000-0000-0000");

        when(memberService.memberSignup(any(SignupReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_EMAIL_FORM));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/member/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(signupReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_EMAIL_FORM, memberException.getExceptionType());
    }

    @Test
    @DisplayName("회원가입 실패(비밀번호 형식 오류)")
    @WithMockUser
    void memberSignupFail4() throws Exception{
        // Given
        SignupReqDTO signupReqDTO = new SignupReqDTO("test@test.com", "3", "test", "000-0000-0000");

        when(memberService.memberSignup(any(SignupReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_PASSWORD_FORM));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/member/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(signupReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_PASSWORD_FORM, memberException.getExceptionType());
    }

    @Test
    @DisplayName("회원가입 실패(연락처 형식 오류)")
    @WithMockUser
    void memberSignupFail5() throws Exception{
        // Given
        SignupReqDTO signupReqDTO = new SignupReqDTO("test@test.com", "hihi123", "test", "0000-0000");

        when(memberService.memberSignup(any(SignupReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_CONTACT_FORM));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/member/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(signupReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_CONTACT_FORM, memberException.getExceptionType());
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void memberLogin() throws Exception {
        // Given
        LoginReqDTO loginReqDTO = new LoginReqDTO("test@test.com", "hihi123");
        ResponseDTO resultDTO = new ResponseDTO<>().ok("로그인 성공!");

        given(memberService.memberLogin(any(LoginReqDTO.class)))
                .willReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginReqDTO))
        );

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인 성공!"))
                .andDo(print())
                .andReturn();

        verify(memberService).memberLogin(any(LoginReqDTO.class));
    }

    @Test
    @DisplayName("로그인 실패(존재하지 않는 회원)")
    @WithMockUser
    void memberLoginFail1() throws Exception {
        // Given
        LoginReqDTO loginReqDTO = new LoginReqDTO("test@test.com", "hihi123");
        when(memberService.memberLogin(any(LoginReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/member/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_NOT_FOUND, memberException.getExceptionType());
    }

    @Test
    @DisplayName("로그인 실패(비밀번호 불일치)")
    @WithMockUser
    void memberLoginFail2() throws Exception {
        // Given
        LoginReqDTO loginReqDTO = new LoginReqDTO("test@test.com", "hihi123");
        when(memberService.memberLogin(any(LoginReqDTO.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_INVALID_PASSWORD));

        // When
        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/member/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginReqDTO)));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_INVALID_PASSWORD, memberException.getExceptionType());
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockUser
    void memberLogout() throws Exception {
        // Given
        LogoutReqDTO logoutReqDTO = LogoutReqDTO.builder()
                .blackAccessToken("blackAccessToken")
                .blackRefreshToken("blackRefreshToken")
                .build();

        ResponseDTO resultDTO = new ResponseDTO<>().ok("로그아웃 성공");

        given(memberService.memberLogout(any(LogoutReqDTO.class)))
                .willReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(logoutReqDTO))
        );

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그아웃 성공"))
                .andDo(print())
                .andReturn();

        verify(memberService).memberLogout(any(LogoutReqDTO.class));
    }

    @Test
    @DisplayName("본인 고유식별자 조회(성공)")
    @WithMockUser
    void getIdentifier() throws Exception {
        // Given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);

        ResponseDTO resultDTO = new ResponseDTO<>().ok("식별번호 조회 성공");

        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(jwtUtil.getUserEmail(any(String.class)))
                .thenReturn("email");
        when(memberService.getIdentifier(any(String.class)))
                .thenReturn(resultDTO);

        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/identifier")
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stateCode").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("식별번호 조회 성공"))
                .andDo(print())
                .andReturn();

        verify(memberService).getIdentifier(any(String.class));
    }

    @Test
    @DisplayName("본인 고유식별자 조회(실패)")
    @WithMockUser
    void getIdentifierFail() throws Exception {
        // Given
        HttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String authorizationHeader = "Bearer your-access-token";
        ((MockHttpServletRequest) httpServletRequest).addHeader("Authorization", authorizationHeader);


        // When
        when(jwtUtil.parseHeader(any(HttpServletRequest.class), eq(HttpHeaders.AUTHORIZATION)))
                .thenReturn("accessToken");
        when(jwtUtil.getUserEmail(any(String.class)))
                .thenReturn("email");
        when(memberService.getIdentifier(any(String.class)))
                .thenThrow(new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));


        Throwable thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.get("/identifier")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .requestAttr(DispatcherServlet.class.getName() + ".REQUEST", httpServletRequest));
        });

        // Then
        assertTrue(thrown.getCause() instanceof MemberException);
        MemberException memberException = (MemberException) thrown.getCause();
        assertEquals(MemberExceptionType.MEMBER_NOT_FOUND, memberException.getExceptionType());
    }
}