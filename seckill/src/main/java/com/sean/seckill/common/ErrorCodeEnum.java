package com.sean.seckill.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author sean
 * @Date 2025/06/18
 * 响应码枚举类
 */
@Getter
@ToString
@AllArgsConstructor
public enum ErrorCodeEnum {
    //常用的错误码
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),

    //登录相关的错误码
    LOGIN_ERROR(500210, "用户名或者密码错误"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    BING_ERROR(500212, "参数绑定异常"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214, "更新密码失败");

    private final Integer code;
    private final String message;
}
