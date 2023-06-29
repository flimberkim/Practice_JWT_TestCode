package com.example.bolta_justin.partner.entity;

import com.example.bolta_justin.partner.enums.PartnerType;
import lombok.*;

import javax.persistence.*;

/**
 * 가맹점 테이블
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_partner")
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long id; // PK

    @Column(name = "partner_type")
    @Enumerated(EnumType.STRING)
    private PartnerType partnerType; // 업종

    @Column(name = "partner_name")
    private String name; // 가맹점 이름

}
