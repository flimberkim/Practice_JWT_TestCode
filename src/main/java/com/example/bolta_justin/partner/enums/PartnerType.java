package com.example.bolta_justin.partner.enums;

public enum PartnerType {
    A("식품"),
    B("화장품"),
    C("식당");

    private String partnerType;

    PartnerType(String partnerType) {
        this.partnerType = partnerType;
    }

    public String getPartnerType(){
        return partnerType;
    }
}
