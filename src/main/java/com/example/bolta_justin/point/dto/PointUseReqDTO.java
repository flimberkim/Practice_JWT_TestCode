package com.example.bolta_justin.point.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class PointUseReqDTO {
   private String pointUseType;
   private Long partnerId;
   private String pointBarcode;
   private int pointAmount;
}
