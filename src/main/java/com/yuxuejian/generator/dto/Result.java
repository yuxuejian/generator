package com.yuxuejian.generator.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    // 1、成功 0、失败
    private int success;
    private String msg;
    private Object data;
    public Result(int success, String msg) {
        this.success = success;
        this.msg = msg;
    }
}
