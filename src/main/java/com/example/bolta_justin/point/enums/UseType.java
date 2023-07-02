package com.example.bolta_justin.point.enums;

public enum UseType {
    use("사용"),
    earn("적립");

    private String useType;

    UseType(String useType) {
        this.useType = useType;
    }

    public String getUseType(){
        return useType;
    }

    public static UseType fromValue(String value){
        for(UseType useType : UseType.values()){
            if(useType.getUseType().equals(value)){
                return useType;
            }
        }
        throw new IllegalArgumentException("Invalid UseType value: " + value);
    }


}
