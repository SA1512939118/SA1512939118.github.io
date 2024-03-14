package com.zh.common.result;

import lombok.Data;

/**
 * @author zh
 * @version 1.0
 * @date 2023/11/12 0:38
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    //构造私有化
    private Result(){};


    //封装返回数据
    public static<T> Result<T> build(T body,ResultCodeEnum resultCodeEnum){
        Result<T> result = new Result<>();

        //封装数据
        if(body != null){
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    //成功
    public static<T> Result<T> ok(){
        return build(null,ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(T data){
        return build(data,ResultCodeEnum.SUCCESS);
    }

    //失败
    public static<T> Result<T> fail(){
        return build(null,ResultCodeEnum.FAIL);
    }

    public static<T> Result<T> fail(T data){
        return build(data,ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }


}
