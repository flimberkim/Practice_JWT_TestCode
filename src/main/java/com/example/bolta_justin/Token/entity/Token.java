package com.example.bolta_justin.Token.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 토큰 블랙리스트
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_token")
public class Token {
    @Id
    @Column(name="blocked_token")
    private String token;
}
