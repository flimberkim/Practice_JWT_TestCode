package com.example.bolta_justin.partner.dto;

import com.example.bolta_justin.partner.entity.Partner;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PartnerDTO {
    private String partnerType;
    private String partnerName;

    public PartnerDTO(String partnerType, String partnerName) {
        this.partnerType = partnerType;
        this.partnerName = partnerName;
    }
    public PartnerDTO(Partner partner) {
        switch (partner.getPartnerType()) {
            case A:
                this.partnerType = "식품";
                break;
            case B:
                this.partnerType = "화장품";
                break;
            case C:
                this.partnerType = "식당";
                break;
        }
        this.partnerName = partner.getName();
    }
}
