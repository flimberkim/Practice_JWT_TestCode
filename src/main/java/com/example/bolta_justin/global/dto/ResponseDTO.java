package com.example.bolta_justin.global.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    private int stateCode;
    private boolean success;
    private String message;
    private T data;

    public ResponseDTO(int stateCode, boolean success, T data, String msg){
        this.stateCode = stateCode;
        this.success = success;
        this.message = msg;
        this.data = data;
    }

    // response 성공시
    public ResponseDTO<T> ok(T data, String msg) {
        return ResponseDTO.<T>builder()
                .stateCode(200)
                .success(true)
                .data(data)
                .message(msg)
                .build();
    }

    // response 성공시 (메세지만 출력)
    public ResponseDTO<T> ok(String msg){
        return ResponseDTO.<T>builder()
                .stateCode(200)
                .success(true)
                .message(msg)
                .build();
    }
}
