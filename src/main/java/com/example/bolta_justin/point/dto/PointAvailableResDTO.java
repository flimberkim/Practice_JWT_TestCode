package com.example.bolta_justin.point.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointAvailableResDTO {
    private String barcode;
    private String aPoint;
    private String bPoint;
    private String cPoint;
}
