package com.example.bolta_justin.member.controller;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LogoutReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @PostMapping("/member/signup")
    public ResponseDTO memberSignup(@RequestBody SignupReqDTO signupReqDTO){
        return memberService.memberSignup(signupReqDTO);
    }

    /**
     * 로그인
     */
    @PostMapping("/member/login")
    public ResponseDTO memberLogin(@RequestBody LoginReqDTO loginReqDTO){
        return memberService.memberLogin(loginReqDTO);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseDTO memberLogout(HttpServletRequest request){
        LogoutReqDTO logoutReqDTO = LogoutReqDTO.builder()
                .blackAccessToken(jwtUtil.parseHeader(request,HttpHeaders.AUTHORIZATION))
                .blackRefreshToken(jwtUtil.parseHeader(request, "REFRESH"))
                .build();
        return memberService.memberLogout(logoutReqDTO);
    }

    /**
     * 본인의 고유식별자 조회
     */
    @GetMapping("/identifier")
    public ResponseDTO getIdentifier(HttpServletRequest request){
        String accessToken = jwtUtil.parseHeader(request, HttpHeaders.AUTHORIZATION);
        String email = jwtUtil.getUserEmail(accessToken);
        return memberService.getIdentifier(email);
    }


}
