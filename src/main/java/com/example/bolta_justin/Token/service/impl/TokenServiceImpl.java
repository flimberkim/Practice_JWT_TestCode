package com.example.bolta_justin.Token.service.impl;

import com.example.bolta_justin.Token.entity.Token;
import com.example.bolta_justin.Token.repository.TokenRepository;
import com.example.bolta_justin.Token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    @Override
    public boolean checkBlackList(String token, HttpServletResponse response) throws IOException {
        Token check = tokenRepository.findByToken(token).orElse(null);


        if(!Objects.isNull(check)){
            //login페이지로 redirect하라는 response
            response.sendRedirect("/member/login");
            return true;
        }
        return false;
    }
}
