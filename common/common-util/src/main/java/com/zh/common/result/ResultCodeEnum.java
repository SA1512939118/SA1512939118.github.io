package com.zh.common.result;

import lombok.Getter;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/12 0:32
 */

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    LOGIN_ERROR(204,"认证失败")
    ;

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }
}
