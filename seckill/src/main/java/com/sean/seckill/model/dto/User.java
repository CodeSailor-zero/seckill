package com.sean.seckill.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sean
 * @Date 2025/44/18
 * 用户实体类
 */
@Data
@TableName("seckill_user")
public class User implements Serializable {
    /**
     * 用户 ID,手机号码
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    private String nickname;
    /**
     * MD5(MD5(pass 明文+固定 salt)+salt)
     */
    private String password;
    private String slat;
    /**
     * 头像
     */
    private String head;
    /**
     * 注册时间
     */
    private Date registerDate;
    /**
     * 最后一次登录时间
     */
    private Date lastLoginDate;
    /**
     * 登录次数
     */
    private Integer loginCount;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
    private static final long serialVersionUID = 5666000L;
}
