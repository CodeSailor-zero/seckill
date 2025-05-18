package com.sean.seckill.exception;

import com.sean.seckill.common.BaseResponse;
import com.sean.seckill.common.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sean
 * @Date 2025/10/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{
    private ErrorCodeEnum errorCodeEnum;
}
