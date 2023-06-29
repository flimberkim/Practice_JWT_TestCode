package com.example.bolta_justin.member.controller;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LoginResDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
