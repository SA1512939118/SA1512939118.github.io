package com.zh.common.config.exception;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/12 22:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZhException extends RuntimeException{
    private Integer code;
    private String msg;
}
