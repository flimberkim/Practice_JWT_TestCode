package com.example.bolta_justin.member.service;

import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LogoutReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;


public interface MemberService {

    ResponseDTO memberSignup(SignupReqDTO signupReqDTO);

    boolean isValidEmail(String email);

    boolean checkEmailForm(String email);

    boolean checkPasswordForm(String password);

    boolean checkContactForm(String contact);

    ResponseDTO memberLogin(LoginReqDTO loginReqDTO);

    boolean checkPassword(String inputPassword, String encodedPassword);

    ResponseDTO memberLogout(LogoutReqDTO logoutReqDTO);

    ResponseDTO getIdentifier(String email);

    String formatNumber(int number);

}
