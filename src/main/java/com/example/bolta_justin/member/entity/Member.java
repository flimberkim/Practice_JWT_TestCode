package com.example.bolta_justin.member.entity;

import com.example.bolta_justin.barcode.entity.Barcode;
import lombok.*;

import javax.persistence.*;

/**
 * 회원 테이블
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_identifier", length = 9, unique = true)
    private Integer identifier; // 회원식별번호

    @OneToOne
    @JoinColumn(name = "barcode_id")
    private Barcode barcode; // 바코드

    @Column(name = "member_email", unique = true)
    private String email; // 이메일

    @Column(name = "member_password")
    private String password; // 비밀번호

    @Column(name = "member_name")
    private String name; // 이름

    @Column(name = "member_contact")
    private String contact; // 연락처

}
