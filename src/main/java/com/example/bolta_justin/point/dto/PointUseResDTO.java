package com.example.bolta_justin.point.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class PointUseResDTO {
    private String barcode;
    private String pointUseType;
    private String partnerType;
    private String partnerName;
    private LocalDateTime pointUseDate;
    private String userIdentifier;
    private int currentPoint;
}
