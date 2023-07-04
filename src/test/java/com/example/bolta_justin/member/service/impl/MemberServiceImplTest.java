package com.example.bolta_justin.member.service.impl;

import com.example.bolta_justin.Token.repository.TokenRepository;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtProperties;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LoginResDTO;
import com.example.bolta_justin.member.dto.LogoutReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.entity.Member;
import com.example.bolta_justin.member.exception.MemberException;
import com.example.bolta_justin.member.exception.MemberExceptionType;
import com.example.bolta_justin.member.repository.MemberRepository;
import com.example.bolta_justin.member.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    MemberServiceImpl memberServiceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    JwtProperties jwtProperties;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    Pattern pattern;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberServiceImpl).build();
    }

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void memberSignup() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("admin1234")
                .name("admin")
                .contact("010-1234-1234")
                .build();


        when(memberRepository.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(null));

        ResponseDTO responseDTO = memberServiceImpl.memberSignup(signupReqDTO);

        assertEquals("회원가입 성공", responseDTO.getMessage());
        verify(memberRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("회원가입 실패(이미 존재하는 회원)")
    @WithMockUser
    void memberSignupFail1() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("admin1234")
                .name("admin")
                .contact("010-1234-1234")
                .build();


        when(memberRepository.findByEmail(any(String.class))).thenThrow(new MemberException(MemberExceptionType.MEMBER_ALREADY_EXISTS)); // 이미 존재하는 회원으로 가정

        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberSignup(signupReqDTO);
        });
        verify(memberRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("회원가입 실패(잘못된 입력값)")
    @WithMockUser
    void memberSignupFail2() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("")
                .password("")
                .name("admin")
                .contact("010-1234-1234")
                .build();

        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberSignup(signupReqDTO);
        });

    }

    @Test
    @DisplayName("회원가입 실패(잘못된 이메일 형식)")
    @WithMockUser
    void memberSignupFail3() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("adminmin.com")
                .password("admin1234")
                .name("admin")
                .contact("010-1234-1234")
                .build();

        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberSignup(signupReqDTO);
        });
    }

    @Test
    @DisplayName("회원가입 실패(잘못된 비밀번호 형식)")
    @WithMockUser
    void memberSignupFail4() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("34")
                .name("admin")
                .contact("010-1234-1234")
                .build();

        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberSignup(signupReqDTO);
        });
    }

    @Test
    @DisplayName("회원가입 실패(잘못된 연락처 형식)")
    @WithMockUser
    void memberSignupFail5() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("admin1234")
                .name("admin")
                .contact("0101234")
                .build();

        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberSignup(signupReqDTO);
        });
    }

    @Test
    @DisplayName("사용 가능한 이메일인지 확인(가능)")
    @WithMockUser
    void isValidEmail() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("admin1234")
                .name("admin")
                .contact("0101234")
                .build();

        when(memberRepository.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(null));

        assertEquals(true, memberServiceImpl.isValidEmail(signupReqDTO.getEmail()));
        verify(memberRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("사용 가능한 이메일인지 확인(불가능)")
    @WithMockUser
    void isValidEmailFail() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("admin1234")
                .name("admin")
                .contact("0101234")
                .build();

        when(memberRepository.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(new Member()));

        assertEquals(false, memberServiceImpl.isValidEmail(signupReqDTO.getEmail()));
        verify(memberRepository).findByEmail(any(String.class));
    }

    @Test
    @DisplayName("이메일 형식 확인(성공)")
    @WithMockUser
    void checkEmailForm() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admin@admin.com")
                .password("admin1234")
                .name("admin")
                .contact("0101234")
                .build();

        assertEquals(true, memberServiceImpl.checkEmailForm(signupReqDTO.getEmail()));
    }

    @Test
    @DisplayName("이메일 형식 확인(실패)")
    @WithMockUser
    void checkEmailFormFail() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admimin.com")
                .password("admin1234")
                .name("admin")
                .contact("0101234")
                .build();

        assertEquals(false, memberServiceImpl.checkEmailForm(signupReqDTO.getEmail()));
    }

    @Test
    @DisplayName("비밀번호 형식 확인(성공)")
    @WithMockUser
    void checkPasswordForm() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admimin.com")
                .password("admin1234")
                .name("admin")
                .contact("0101234")
                .build();

        assertEquals(true, memberServiceImpl.checkPasswordForm(signupReqDTO.getPassword()));
    }

    @Test
    @DisplayName("비밀번호 형식 확인(실패)")
    @WithMockUser
    void checkPasswordFormFail() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admimin.com")
                .password("a")
                .name("admin")
                .contact("0101234")
                .build();

        assertEquals(false, memberServiceImpl.checkPasswordForm(signupReqDTO.getPassword()));
    }

    @Test
    @DisplayName("연락처 형식 확인(성공)")
    @WithMockUser
    void checkContactForm() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admimin.com")
                .password("admin1234")
                .name("admin")
                .contact("010-1234-1111")
                .build();

        assertEquals(true, memberServiceImpl.checkContactForm(signupReqDTO.getContact()));
    }

    @Test
    @DisplayName("연락처 형식 확인(실패)")
    @WithMockUser
    void checkContact() throws Exception {
        SignupReqDTO signupReqDTO = SignupReqDTO.builder()
                .email("admimin.com")
                .password("admin1234")
                .name("admin")
                .contact("010-12341111")
                .build();

        assertEquals(false, memberServiceImpl.checkContactForm(signupReqDTO.getContact()));
    }

    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void memberLogin() throws Exception {
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("user@user.com")
                .password("user1234")
                .build();
        Member tempMember = Member.builder()
                .identifier(1)
                .email("user@user.com")
                .name("사용자")
                .password(passwordEncoder.encode("user1234"))
                .build();

        ResponseDTO resultDTO = new ResponseDTO(200, true, LoginResDTO.builder()
                .email("user@user.com")
                .role("USER")
                .name("사용자")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build(), "로그인 성공!");

        when(memberRepository.existsMemberByEmail(loginReqDTO.getEmail())).thenReturn(true);
        when(memberServiceImpl.checkPassword(loginReqDTO.getPassword(), passwordEncoder.encode("user1234"))).thenReturn(true);
        when(memberRepository.findByEmail(loginReqDTO.getEmail())).thenReturn(Optional.ofNullable(tempMember));
        when(jwtUtil.createAccessToken(tempMember.getEmail(), jwtProperties.getSecretKey(), "USER", tempMember.getIdentifier())).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken(tempMember.getEmail(), jwtProperties.getSecretKey(), "USER", tempMember.getIdentifier())).thenReturn("refreshToken");
        assertEquals("로그인 성공!", memberServiceImpl.memberLogin(loginReqDTO).getMessage());
    }

    @Test
    @DisplayName("로그인 실패(존재하지 않는 회원)")
    @WithMockUser
    void memberLoginFail1() throws Exception {
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("user@user.com")
                .password("user1234")
                .build();

        when(memberRepository.existsMemberByEmail(loginReqDTO.getEmail())).thenReturn(false);
        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberLogin(loginReqDTO);
        });
    }

    @Test
    @DisplayName("로그인 실패(잘못된 비밀번호)")
    @WithMockUser
    void memberLoginFail2() throws Exception {
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("user@user.com")
                .password("user1234")
                .build();

        when(memberRepository.existsMemberByEmail(loginReqDTO.getEmail())).thenReturn(true);
        when(memberRepository.findByEmail(loginReqDTO.getEmail())).thenReturn(Optional.of(new Member()));
        when(memberServiceImpl.checkPassword(loginReqDTO.getPassword(), passwordEncoder.encode("user1234"))).thenReturn(false);
        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.memberLogin(loginReqDTO);
        });
    }

    @Test
    @DisplayName("비밀번호 일치여부 확인(성공)")
    @WithMockUser
    void checkPassword() throws Exception {
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("user@user.com")
                .password("user1234")
                .build();

        when(memberServiceImpl.checkPassword(loginReqDTO.getPassword(), passwordEncoder.encode(loginReqDTO.getPassword()))).thenReturn(true);

        assertEquals(true, memberServiceImpl.checkPassword(loginReqDTO.getPassword(), passwordEncoder.encode(loginReqDTO.getPassword())));
    }

    @Test
    @DisplayName("비밀번호 일치여부 확인(실패)")
    @WithMockUser
    void checkPasswordFail() throws Exception {
        LoginReqDTO loginReqDTO = LoginReqDTO.builder()
                .email("user@user.com")
                .password("user1234")
                .build();

        assertEquals(false, memberServiceImpl.checkPassword(passwordEncoder.encode(loginReqDTO.getPassword()), passwordEncoder.encode("잘못된 비밀번호")));
    }

    @Test
    @DisplayName("로그아웃")
    @WithMockUser
    void memberLogout() throws Exception {
        LogoutReqDTO logoutReqDTO = LogoutReqDTO.builder()
                .blackAccessToken("blackAccessToken")
                .blackRefreshToken("blackRefreshToken")
                .build();

        assertEquals("로그아웃 성공", memberServiceImpl.memberLogout(logoutReqDTO).getMessage());
    }

    @Test
    @DisplayName("회원 고유식별자 조회(성공)")
    @WithMockUser
    void getIdentifier() throws Exception {

        when(memberRepository.findByEmail("email")).thenReturn(Optional.ofNullable(Member.builder()
                .identifier(1)
                .build()));

        assertEquals("000000001", memberServiceImpl.getIdentifier("email").getData());
    }

    @Test
    @DisplayName("회원 고유식별자 조회(실패)")
    @WithMockUser
    void getIdentifierFail() throws Exception {

        Assertions.assertThrows(MemberException.class, () -> {
            memberServiceImpl.getIdentifier("emailFail");
        });

    }

    @Test
    @DisplayName("회원 고유식별자 값 생성(부족한 자리 수 채우기, 성공)")
    @WithMockUser
    void formatNumber() throws Exception {
        assertEquals("000000001", memberServiceImpl.formatNumber(1));
    }

}