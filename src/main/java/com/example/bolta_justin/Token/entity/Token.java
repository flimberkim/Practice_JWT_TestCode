package com.example.bolta_justin.Token.entity;

import lombok.*;

import javax.persistence.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Long id; // PK

    @Column(name="blocked_token", columnDefinition = "VARCHAR(500)")
    private String token;
}
