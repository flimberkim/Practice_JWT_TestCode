package com.example.bolta_justin.global.jwt;

import com.example.bolta_justin.global.jwt.exception.JwtUtilException;
import com.example.bolta_justin.global.jwt.exception.JwtUtilExceptionType;
import com.example.bolta_justin.member.dto.LoginResDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 헤더값이 유효한지 검증
     */
    public boolean checkHeader(String header) {
        if (header == null || !header.startsWith(jwtProperties.getTokenPrefix())) {
            logger.error("없는 토큰 또는 잘못된 형식의 토큰입니다.");
            return true;
        }
        return false;
    }

    /**
     * 헤더로부터 ResponseDTO 생성
     */
    public LoginResDTO getResDTO(String authorizationHeader) {
        checkHeader(authorizationHeader);
        String token = "";
        Claims claims = null;
        try {
            token = authorizationHeader.replace("Bearer ", "");
            claims = parseToken(token);
            return new LoginResDTO(claims);
        } catch (Exception e) {
            throw new JwtUtilException(JwtUtilExceptionType.INVALID_TOKEN);
        }
    }

    /**
     * Bearer 빼고 토큰값만 가져오는 메서드
     */
    //request header 파싱
    public String parseHeader(HttpServletRequest request, String type){
        String authorization = request.getHeader(type);

        //토큰 검증 후 파싱하기
        if(StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")){
            return authorization.replace("Bearer ","");
        }

        return null;
    }

    /**
     * Token 값을 Claims로 바꿔주는 메서드
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (Exception e){
            throw new JwtUtilException(JwtUtilExceptionType.ACCESS_TOKEN_EXPIRATION_DATE);
        }
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRole(String token) {
        try{
            return Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token)
                    .getBody().get("role", String.class);
        }catch (ExpiredJwtException e){
            throw new JwtUtilException(JwtUtilExceptionType.ACCESS_TOKEN_EXPIRATION_DATE);
        }
    }

    /**
     * 토큰 만료 확인
     */
    public static boolean isTokenExpired(String token, String secretKey) {
        try{
            System.out.println(Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration());
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                    .getBody().getExpiration().before(new Date());
        }
        catch(ExpiredJwtException e){
            return true;
        }
    }

    /**
     * Access 토큰 생성
     */
    public String createAccessToken(String userEmail, String secretKey, String role, Integer identifier) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("userEmail", userEmail);
        claims.put("role", role);
        claims.put("identifier", identifier);
        claims.put("issuer", jwtProperties.getIssuer());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(60).toMillis())) //테스트를 위해 1분으로 해놓음. 원랜 5분
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Refresh 토큰 생성
     */
    public String createRefreshToken(String userEmail, String secretKey, String role, Integer identifier) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("userEmail", userEmail);
        claims.put("role", role);
        claims.put("identifier", identifier);
        claims.put("issuer", jwtProperties.getIssuer());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Duration.ofDays(14).toMillis()))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 토큰에서 identifier값 추출
     */
    public Integer getIdentifier(String token) {
        try{
            return Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token)
                    .getBody().get("identifier", Integer.class);

        } catch (ExpiredJwtException e){
            throw new JwtUtilException(JwtUtilExceptionType.ACCESS_TOKEN_EXPIRATION_DATE);
        }
    }

    /**
     * 토큰에서 유효기간 일시 추출
     */
    public Date getExpiration(String token) {
        try{
            return Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token)
                    .getBody().getExpiration();
        }catch (ExpiredJwtException e){
            throw new JwtUtilException(JwtUtilExceptionType.ACCESS_TOKEN_EXPIRATION_DATE);
        }
    }

    /**
     * 토큰에서 Email 추출
     */
    public String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token)
                .getBody().get("userEmail", String.class);
    }


}
