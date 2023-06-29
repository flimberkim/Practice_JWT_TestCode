package com.example.bolta_justin.member.controller;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LogoutReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

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
                .authorizationHeader(request.getHeader(HttpHeaders.AUTHORIZATION))
                .refreshHeader(request.getHeader("REFRESH"))
                .build();
        return memberService.memberLogout(logoutReqDTO);
    }
}
