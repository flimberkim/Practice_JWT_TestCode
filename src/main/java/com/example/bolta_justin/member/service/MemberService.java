package com.example.bolta_justin.member.service;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;

public interface MemberService {

    public ResponseDTO memberSignup(SignupReqDTO signupReqDTO);

    public boolean isValidEmail(String email);

    public boolean checkEmailForm(String email);

    public boolean checkPasswordForm(String password);

    public boolean checkContactForm(String contact);
}
