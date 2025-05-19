package com.sean.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author sean
 * @Date 2025/23/18
 * MD5Util 工具类，根据用户表中的密码提供相对应的方法
 */
public class MD5Util {
    // 盐值
    private static final String slat = "ptqtXy16";
    /**
     * 对密码进行加密
     * @param src 传入的密码
     * @return 加密后的密码
     */
    public static String getMD5(String src){
        return DigestUtils.md5Hex(src);
    }

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
     * 完成前端 发送到后端的 密码 + salt 的加密
     * @param inputPass 前端传入的密码
     * @return 中间密码的加密【md5(password 明文 + slat1)】
     */
    public static String inputPassToMidPassAndSlat(String inputPass,String slat){
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
        String midPass = inputPassToMidPassAndSlat(inputPass,slat);
        return midPassToDBPass(midPass,slat);
    }

    public static void main(String[] args) {
        //中间密码75d7eacdc4a31d0c8c939112ae5880cf
        //库中密码60754534a3224ee1bac15befc974f16d

        //54affc0b46e2159092ff7a653d44a939

        String pass = "123456";
        String slat = "ptqtXy16";
        String str = inputPassToDBPass(pass, slat);
        System.out.println("直接存入数据库的：" + str);
        String midpass = inputPassToMidPassAndSlat(pass, slat);
        System.out.println("中间值：" + midpass);
        String s = midPassToDBPass(midpass, slat);
        System.out.println("正常存入的：" + s);
    }
}
