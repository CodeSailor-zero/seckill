package com.sean.seckill.exception;

import com.sean.seckill.common.BaseResponse;
import com.sean.seckill.common.ErrorCodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

/**
 * @author sean
 * @Date 2025/14/18
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理所有的异常
    @ExceptionHandler(Exception.class)
    public BaseResponse ExceptionHandler(Exception e) {
        //如果是全局异常，正常处理
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return BaseResponse.error(ex.getErrorCodeEnum());
        } else if (e instanceof BindException) {
            //如果是绑定异常 ：由于我们自定义的注解只会在控制台打印错误信息，
            // 想让改信息传给前端。
            //需要获取改异常 BindException，进行打印
            BindException ex = (BindException) e;
            BaseResponse baseResponse = BaseResponse.error(ErrorCodeEnum.BING_ERROR);
            baseResponse.setMessage(" 参 数 校 验 异常~ ：" + ex.getMessage());
            return baseResponse;
        }
        return BaseResponse.error(ErrorCodeEnum.ERROR);
    }
}
