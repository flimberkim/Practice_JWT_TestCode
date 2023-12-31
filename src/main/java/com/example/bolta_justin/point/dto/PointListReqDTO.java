package com.example.bolta_justin.point.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class PointListReqDTO {
    private String startDate;
    private String endDate;
    private String barcode;
}
