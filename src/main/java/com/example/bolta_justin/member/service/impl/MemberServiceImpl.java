package com.example.bolta_justin.member.service.impl;

import com.example.bolta_justin.Token.entity.Token;
import com.example.bolta_justin.Token.repository.TokenRepository;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.JwtProperties;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.member.dto.LoginReqDTO;
import com.example.bolta_justin.member.dto.LoginResDTO;
import com.example.bolta_justin.member.dto.LogoutReqDTO;
import com.example.bolta_justin.member.dto.SignupReqDTO;
import com.example.bolta_justin.member.entity.Member;
import com.example.bolta_justin.member.repository.MemberRepository;
import com.example.bolta_justin.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    //비밀번호 형식은 5자리 이상 문자 숫자 조합
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9]).{5,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    //전화번호 형식은 000-0000-0000
    private static final String CONTACT_REGEX = "^\\d{3}-\\d{4}-\\d{4}$";
    private static final Pattern CONTACT_PATTERN = Pattern.compile(CONTACT_REGEX);


    @Override
    public ResponseDTO memberSignup(SignupReqDTO signupReqDTO) {
        String email = signupReqDTO.getEmail();
        String password = signupReqDTO.getPassword();
        String contact = signupReqDTO.getContact();

        if(email == null || password == null || contact == null){
            return ResponseDTO.builder()
                    .stateCode(400)
                    .success(false)
                    .message("모든 정보를 입력해주세요.")
                    .data(null)
                    .build();
        }

        if(!isValidEmail(email)){
            return ResponseDTO.builder()
                    .stateCode(400)
                    .success(false)
                    .message("이미 존재하는 이메일입니다.")
                    .data(null)
                    .build();
        }
        if(!checkEmailForm(email)){
            return ResponseDTO.builder()
                    .stateCode(400)
                    .success(false)
                    .message("잘못된 이메일 형식입니다.")
                    .data(null)
                    .build();
        }
        if(!checkPasswordForm(password)){
            return ResponseDTO.builder()
                    .stateCode(400)
                    .success(false)
                    .message("비밀번호는 5자리 이상의 문자와 숫자를 포함해주세요.")
                    .data(null)
                    .build();
        }
        if(!checkContactForm(contact)){
            return ResponseDTO.builder()
                    .stateCode(400)
                    .success(false)
                    .message("000-0000-0000 형식으로 입력해 주세요.")
                    .data(null)
                    .build();
        }
        signupReqDTO.setPassword(passwordEncoder.encode(signupReqDTO.getPassword()));
        memberRepository.save(signupReqDTO.toEntity());
        return new ResponseDTO<>().ok("회원가입 성공");
    }

    @Override
    public boolean isValidEmail(String email) {
        Optional<Member> tempMember = memberRepository.findByEmail(email);
        return tempMember.isPresent() ? false : true;
    }

    @Override
    public boolean checkEmailForm(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean checkPasswordForm(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean checkContactForm(String contact) {
        return CONTACT_PATTERN.matcher(contact).matches();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDTO memberLogin(LoginReqDTO loginReqDTO) {
        if(!memberRepository.existsMemberByEmail(loginReqDTO.getEmail())){
            return ResponseDTO.builder()
                    .stateCode(401)
                    .success(false)
                    .message("존재하지 않는 아이디(이메일)입니다.")
                    .data("INVALID_EMAIL")
                    .build();
        }

        Member findMember = memberRepository.findByEmail(loginReqDTO.getEmail()).orElseThrow();

        if(!checkPassword(loginReqDTO.getPassword(), findMember.getPassword())){
            return ResponseDTO.builder()
                    .stateCode(401)
                    .success(false)
                    .message("비밀번호가 일치하지 않습니다.")
                    .data("INVALID_PASSWORD")
                    .build();
        }

        String accessToken = jwtUtil.createAccessToken(findMember.getEmail(), jwtProperties.getSecretKey(), "USER", findMember.getIdentifier());
        String refreshToken = jwtUtil.createRefreshToken(findMember.getEmail(), jwtProperties.getSecretKey(), "USER", findMember.getIdentifier());
        return new ResponseDTO(200, true, LoginResDTO.builder()
                .email(findMember.getEmail())
                .role("USER")
                .name(findMember.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build(), "로그인 성공!");
    }

    @Override
    public boolean checkPassword(String inputPassword, String encodedPassword) {
        return passwordEncoder.matches(inputPassword, encodedPassword);
    }

    @Override
    public ResponseDTO memberLogout(LogoutReqDTO logoutReqDTO) {
        String accessToken = jwtUtil.extractToken(logoutReqDTO.getAuthorizationHeader());
        String refreshToken = jwtUtil.extractToken(logoutReqDTO.getRefreshHeader());

        Token blackAccessToken = new Token(accessToken);
        Token blackRefreshToken = new Token(refreshToken);

        tokenRepository.save(blackAccessToken);
        tokenRepository.save(blackRefreshToken);

        return ResponseDTO.builder()
                .stateCode(200)
                .success(true)
                .message("로그아웃 성공")
                .data(null)
                .build();
    }


}
