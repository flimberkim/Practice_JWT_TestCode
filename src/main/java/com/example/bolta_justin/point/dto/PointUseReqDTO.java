package com.example.bolta_justin.point.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class PointUseReqDTO {
   private String pointUseType;
   private Long partnerId;
   private String pointBarcode;
   private int pointAmount;

}
