package com.sean.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author sean
 * @Date 2025/18/18
 * 自定义注解，对手机号码进行校验
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE }) //注解可以使用的位置
@Retention(RUNTIME) //指定注解的保留策略
@Documented //指定注解会被包含在javadoc中
@Constraint(validatedBy = {IsMobileValidator.class}) //指定注解的验证器
public @interface IsMobile {
    String message() default "手机号码格式错误";
    boolean required() default true; //是否是必须传入手机号码
    Class<?>[] groups() default { };//默认参数
    Class<? extends Payload>[] payload() default { };//默认参数
}
