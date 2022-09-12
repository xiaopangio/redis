package com.xiaobai.redismulticache.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * className: Result
 * description:
 * author: xiaopangio
 * date: 2022/9/12 16:21
 * version: 1.0
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(int code, T data) {
        this.code = code;
        this.data = data;
        this.msg = "success";
    }
}
