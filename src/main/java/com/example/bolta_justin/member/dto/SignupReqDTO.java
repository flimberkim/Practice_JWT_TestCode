package com.example.bolta_justin.member.dto;

import com.example.bolta_justin.member.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupReqDTO {
    private String email;
    private String password;
    private String name;
    private String contact;

    public Member toEntity(){
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .contact(this.contact)
                .build();
    }
}
