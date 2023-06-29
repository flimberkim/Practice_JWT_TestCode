package com.example.bolta_justin.member.dto;

import io.jsonwebtoken.Claims;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResDTO {
    private String email;
    private String role;
    private String name;
    private String accessToken;
    private String refreshToken;

    public LoginResDTO(Claims claims){
        this.email = claims.get("userEmail", String.class);
        this.role = claims.get("role", String.class);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

}
