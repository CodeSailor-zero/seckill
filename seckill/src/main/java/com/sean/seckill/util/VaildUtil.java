package com.sean.seckill.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author sean
 * @Date 2025/24/18
 * 完成一些参数的校验工作
 */
public class VaildUtil {
    //检验手机号码的正则表达式规则
    private static final Pattern MOBILE_PATTER = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 校验手机号码是否合规
     * @param mobile 手机号码
     * @return true 合规，false 不合规
     */
    public static boolean isMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        return MOBILE_PATTER.matcher(mobile).matches();
    }
}
