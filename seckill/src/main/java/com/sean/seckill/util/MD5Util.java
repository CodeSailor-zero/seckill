package com.sean.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author sean
 * @Date 2025/23/18
 * MD5Util 工具类，根据用户表中的密码提供相对应的方法
 */
public class MD5Util {
    /**
     * 对密码进行加密
     * @param src 传入的密码
     * @return 加密后的密码
     */
    public static String getMD5(String src){
        return DigestUtils.md5Hex(src);
    }
    // 盐值
    private static final String slat = "1G02j3U4";

    /**
     * 完成前端 发送到后端的 密码 + salt 的加密
     * @param inputPass 前端传入的密码
     * @return 中间密码的加密【md5(password 明文 + slat1)】
     */
    public static String inputPassToMidPass(String inputPass){
        String str = slat.charAt(0) + inputPass + slat.charAt(2);
         return getMD5(str);
    }

    /**
     * 完成中间密码 + salt 的加密
     * @param midPass 中间密码 【md5(password 明文 + slat1)】
     * @param slat 盐值【每个用户有不同的盐值】
     * @return 数据库密码的加密【md5(md5(password 明文 + slat1) + slat2)】
     */
    public static String midPassToDBPass(String midPass,String slat){
        String str = slat.charAt(0) + midPass + slat.charAt(2);
        return getMD5(str);
    }

    /**
     * 完成前端密码到数据库密码的加密
     * @param inputPass 输入的密码
     * @param slat 盐值
     * @return 数据库密码的加密【md5(md5(password 明文 + slat1) + slat2)】
     */
    public static String inputPassToDBPass(String inputPass,String slat){
        String midPass = inputPassToMidPass(inputPass);
        return midPassToDBPass(midPass,slat);
    }
}
