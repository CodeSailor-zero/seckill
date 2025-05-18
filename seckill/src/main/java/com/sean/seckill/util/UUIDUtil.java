package com.sean.seckill.util;

import java.util.UUID;

/**
 * @author sean
 * @Date 2025/46/18
 * UUID工具类 生成用户票据
 */
public class UUIDUtil {
    /**
     * 生成一个随机的UUID，并去掉其中的“-”
     * @return 随机的UUID
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
