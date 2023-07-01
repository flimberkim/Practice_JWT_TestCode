package com.example.bolta_justin.barcode.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 바코드 테이블
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_barcode")
public class Barcode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "barcode_id")
    private Long id; // PK

    @Column(name = "barcode_barcode", unique = true)
    private String barcode; // 멤버십 바코드

    @Column(name = "barcode_apoint")
    private int aPoint; // A업종(식품) 포인트

    @Column(name = "barcode_bpoint")
    private int bPoint; // B업종(화장품) 포인트

    @Column(name = "barcode_cpoint")
    private int cPoint; // C업종(식당) 포인트

}
