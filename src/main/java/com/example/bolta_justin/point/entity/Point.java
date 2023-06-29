package com.example.bolta_justin.point.entity;

import com.example.bolta_justin.barcode.Barcode;
import com.example.bolta_justin.partner.enums.PartnerType;
import com.example.bolta_justin.point.enums.UseType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 포인트 테이블
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_point")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id")
    private Barcode barcode; // 바코드

    @Column(name = "point_type")
    @Enumerated(EnumType.STRING)
    private UseType useType; // 포인트 사용 타입

    @Column(name = "point_category")
    @Enumerated(EnumType.STRING)
    private PartnerType partnerType; // 포인트 사용 업종

    @Column(name = "point_partner_name")
    private String partnerName; // 포인트 사용 가맹점 이름

    @CreatedDate
    @Column(name = "point_date", updatable = false)
    private LocalDateTime pointDate; // 포인트 사용/적립 날짜

    @Column(name = "point_user_identifier")
    private int userIdentifier; // 포인트 사용/적립 회원

    @Column(name = "point_current")
    private int currentPoint; // 포인트 사용/적립 회원의 현재 포인트

}
