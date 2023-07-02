package com.example.bolta_justin.global.jwt;

import com.example.bolta_justin.Token.repository.TokenRepository;
import com.example.bolta_justin.Token.service.TokenService;
import com.example.bolta_justin.global.dto.ResponseDTO;
import com.example.bolta_justin.global.jwt.exception.JwtUtilException;
import com.example.bolta_justin.global.jwt.exception.JwtUtilExceptionType;
import com.example.bolta_justin.member.dto.LoginResDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
@Getter
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    /**
     * 허용할 url 세팅
     */
    private HashMap<String, String> permitUrl = new HashMap<>(){{
        put("/member/login","permit");
        put("/member/signup","permit");
    }};



    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    @Autowired
    public JwtFilter(JwtUtil jwtUtil, JwtProperties jwtProperties, TokenRepository tokenRepository, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }

    public static JwtFilter of(JwtUtil jwtUtil,JwtProperties jwtProperties, TokenRepository tokenRepository, TokenService tokenService){
        return new JwtFilter(jwtUtil, jwtProperties, tokenRepository, tokenService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT filter run");
//
        String tokenStr = jwtUtil.parseHeader(request, HttpHeaders.AUTHORIZATION);
        String refreshHeader = request.getHeader("REFRESH");

        String uri = request.getRequestURI();
        if(permitUrl.containsKey(uri) || uri.startsWith("/h2-console")){
            filterChain.doFilter(request, response);
            return;
        }

        if(tokenStr != null && !tokenStr.equalsIgnoreCase("null")){

            //black list 체크
            if(tokenService.checkBlackList(tokenStr, response)) throw new JwtUtilException(JwtUtilExceptionType.INVALID_TOKEN);
            //access token 만료시간 체크
            if(jwtUtil.isTokenExpired(tokenStr, jwtProperties.getSecretKey())) {
                //refresh header 있는지 여부 확인
                if(refreshHeader == null){
                    throw new JwtUtilException(JwtUtilExceptionType.ACCESS_TOKEN_EXPIRATION_DATE);
                }
                //refresh token
                String refresh = jwtUtil.parseHeader(request, "REFRESH");
                if(tokenService.checkBlackList(refresh, response)) throw new JwtUtilException(JwtUtilExceptionType.INVALID_TOKEN);
                if(jwtUtil.isTokenExpired(refresh, jwtProperties.getSecretKey())){
                    throw new JwtUtilException(JwtUtilExceptionType.REFRESH_TOKEN_EXPIRATION_DATE);
                }
                ObjectMapper om = new ObjectMapper();
                String email = jwtUtil.getUserEmail(refresh);
                String role = jwtUtil.getRole(refresh);
                Integer id = jwtUtil.getIdentifier(refresh);
                String validAccessToken = jwtUtil.createAccessToken(email,jwtProperties.getSecretKey(),role,id);
                ResponseDTO responseDTO = ResponseDTO.builder()
                        .stateCode(200).success(true).data(validAccessToken).message("New AccessToken").build();

                //(DTO -> json)
                String jsonStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(responseDTO);
                response.getWriter().write(jsonStr);
                return;
            }
            LoginResDTO user = jwtUtil.getResDTO(request.getHeader(HttpHeaders.AUTHORIZATION));
            SecurityContextHolder.getContext()
                    .setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    "",
                                    user.getAuthorities()
                            )
                    );
            filterChain.doFilter(request,response);
            return;
        }

        throw new JwtUtilException(JwtUtilExceptionType.ACCESS_TOKEN_UN_AUTHORIZED);
    }
}
