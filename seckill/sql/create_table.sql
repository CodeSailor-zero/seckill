-- 切换库
use seckill;

-- 创建用户表
CREATE TABLE `seckill_user`
(
    `id`              BIGINT(20)   NOT NULL COMMENT '用户 ID, 设为主键, 唯一手机号',
    `nickname`        VARCHAR(255) NOT NULL DEFAULT '',
    `password`        VARCHAR(32)  NOT NULL DEFAULT '' COMMENT 'MD5(MD5(pass 明文+固定salt)+salt)',
    `slat`            VARCHAR(10)  NOT NULL DEFAULT '',
    `head`            VARCHAR(128) NOT NULL DEFAULT '' COMMENT '头像',
    `register_date`   DATETIME                DEFAULT CURRENT_TIMESTAMP NOT NULL  COMMENT '注册时间',
    `last_login_date` DATETIME              DEFAULT NULL COMMENT '最后一次登录时间',
    `login_count`     INT(11)               DEFAULT '0' COMMENT '登录次数',
    updateTime        datetime              default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete          tinyint               default 0 not null comment '是否删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;