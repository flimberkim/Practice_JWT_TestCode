package com.example.bolta_justin.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginReqDTO {
    private String email;
    private String password;
}
