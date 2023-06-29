package com.example.bolta_justin.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogoutReqDTO {
    private String authorizationHeader;
    private String refreshHeader;
}
