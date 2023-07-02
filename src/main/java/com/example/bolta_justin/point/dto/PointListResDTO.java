package com.example.bolta_justin.point.dto;

import com.example.bolta_justin.point.entity.Point;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class PointListResDTO {
    private String updateDate;
    private String useType;
    private String category;
    private String partnerName;

    private int leftPoint;

    public PointListResDTO(Point point){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        this.updateDate = point.getPointDate().format(formatter);
        this.useType = point.getUseType().getUseType();
        this.category = point.getPartner().getPartnerType().getPartnerType();
        this.partnerName = point.getPartner().getName();
        this.leftPoint = point.getCurrentPoint();
    }
}
