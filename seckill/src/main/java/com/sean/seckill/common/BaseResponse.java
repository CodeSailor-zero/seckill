package com.sean.seckill.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sean
 * @Date 2025/12/18
 * 响应返回类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    private long code;
    private String message;
    private Object obj;


    /**
     * 成功，-同时携带数据
     * @param obj 传进来的数据
     * @return 返回一个BaseResponse对象
     */
    public static BaseResponse success(Object obj) {
        return new BaseResponse(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMessage(), obj);
    }

    /**
     * 成功，-不携带数据
     * @return 返回一个BaseResponse对象
     */
    public static BaseResponse success() {
        return new BaseResponse(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getMessage(),null);
    }
    /**
     * 失败，-同时携带数据
     * @param code 错误码
     * @param obj 传进来的数据
     * @return 返回一个BaseResponse对象
     */
    public static BaseResponse error(ErrorCodeEnum code, Object obj) {
        return new BaseResponse(code.getCode(), code.getMessage(), obj);
    }
    /**
     * 失败，-不携带数据
     * @param code 错误码
     * @return 返回一个BaseResponse对象
     */
    public static BaseResponse error(ErrorCodeEnum code) {
        return new BaseResponse(code.getCode(), code.getMessage(), null);
    }
}
