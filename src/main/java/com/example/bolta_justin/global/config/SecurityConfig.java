package com.example.bolta_justin.global.config;

import com.example.bolta_justin.Token.repository.TokenRepository;
import com.example.bolta_justin.global.jwt.JwtFilter;
import com.example.bolta_justin.global.jwt.JwtProperties;
import com.example.bolta_justin.global.jwt.JwtUtil;
import com.example.bolta_justin.global.jwt.exception.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    /**
     * 토큰을 사용하지 않는 허용 url
     */
    String[] permitUrl = {"/member/login","/member/signup","/h2-console"};

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        return http
                .authorizeRequests()
                .mvcMatchers(permitUrl)
                .permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()

                .and()
                .cors()//기본 cors 설정
                .and()
                .csrf().disable() //위조요청 방지 비활성화
                .formLogin().disable() //formLogin 인증 비활성화
                .httpBasic().disable() //httpBasic 인증 비활성화
                .logout().disable() // "/logout" url을 쓰기 위한 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .addFilterBefore(JwtFilter.of(jwtUtil, jwtProperties, tokenRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(JwtExceptionFilter.of(jwtUtil, jwtProperties), JwtFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().mvcMatchers(permitUrl);
    }



}
