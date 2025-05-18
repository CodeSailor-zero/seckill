package com.sean.seckill.model.request;

import com.sean.seckill.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author sean
 * @Date 2025/21/18
 * 登录请求类
 */
@Data
public class LoginRequest {
    @NotNull
    @IsMobile //自定义的注解
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;
}
