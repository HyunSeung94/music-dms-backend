package com.mesim.sc.api;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApiResponseDto implements Serializable {

    private boolean success;
    private Object data;
    private String message;

    public ApiResponseDto(){}

    public ApiResponseDto(boolean success){
        this.success = success;
    }

    public ApiResponseDto(boolean success, Object data){
        this.success = success;
        this.data = data;
    }

    public ApiResponseDto(boolean success, Object data, String message){
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
