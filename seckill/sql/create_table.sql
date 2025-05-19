-- 切换库
use seckill;

-- 创建用户表
DROP TABLE IF EXISTS `seckill_user`;
CREATE TABLE `seckill_user`
(
    id              bigint                                 not null comment '用户 ID, 设为主键, 唯一手机号'
        primary key,
    nickname        varchar(255) default ''                not null,
    password        varchar(32)  default ''                not null comment 'MD5(MD5(pass 明文+固定salt)+salt)',
    slat            varchar(10)  default ''                not null,
    head            varchar(128) default ''                not null comment '头像',
    register_date   datetime     default CURRENT_TIMESTAMP not null comment '注册时间',
    last_login_date datetime                               null comment '最后一次登录时间',
    login_count     int          default 0                 null comment '登录次数',
    update_time     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint      default 0                 not null comment '是否删除'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 商品表
DROP TABLE IF EXISTS `goods`;
create table goods
(
    id           bigint auto_increment comment '商品 id'
        primary key,
    goods_name   varchar(16)    default '商品名'          not null,
    goods_title  varchar(64)    default ''                not null comment '商品标题',
    goods_img    varchar(64)    default ''                not null comment '商品图片',
    goods_detail longtext                                 not null comment '商品详情',
    goods_price  decimal(10, 2) default 0.00              null comment '商品价格',
    goods_stock  int            default 0                 null comment '商品库存',
    create_time  datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint        default 0                 not null comment '是否删除'
);

-- 秒杀商品表
DROP TABLE IF EXISTS `seckill_goods`;
create table seckill_goods
(
    id            bigint auto_increment comment '秒杀商品id'
        primary key,
    goods_id      bigint         default 0                 null comment '商品id 对应 商品表的主键',
    seckill_price decimal(10, 2) default 0.00              null comment '秒杀价格',
    stock_count   int(10)        default 0                 null comment '秒杀商品库存',
    start_date    datetime                                 null comment '秒杀商品开始时间',
    end_date      datetime                                 null comment '秒杀商品结束时间',
    create_time   datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint        default 0                 not null comment '是否删除'
);


-- 订单详情表
CREATE TABLE orders
(
    `id`               BIGINT(20)     NOT NULL AUTO_INCREMENT,
    `user_id`          BIGINT(20)     NOT NULL DEFAULT 0 COMMENT '用户id',
    `goods_id`         BIGINT(20)     NOT NULL DEFAULT 0 COMMENT '商品id',
    `delivery_addr_id` BIGINT(20)     NOT NULL DEFAULT 0 COMMENT '用户地址id',
    `goods_name`       VARCHAR(16)    NOT NULL DEFAULT '' COMMENT '商品名字',
    `goods_count`      INT(11)        NOT NULL DEFAULT '0' COMMENT '下单商品数量',
    `goods_price`      DECIMAL(10, 2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
    `order_channel`    TINYINT(4)     NOT NULL DEFAULT '0' COMMENT '订单渠道1pc，2Android，3ios',
    `status`           TINYINT(4)     NOT NULL DEFAULT '0' COMMENT '订单状态：0 新建未支付1已支付2 已发货 3 已收货 4 已退款 5 已完成',
    `create_date`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    `pay_date`         DATETIME       DEFAULT NULL,
    update_time        DATETIME       DEFAULT CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete          TINYINT        DEFAULT 0                 not null comment '是否删除',
    PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=600 DEFAULT CHARSET=utf8mb4

-- 秒杀订单表
CREATE TABLE `seckill_order`
(
    `id`       BIGINT(20) NOT NULL AUTO_INCREMENT,
    `user_id`  BIGINT(20) NOT NULL DEFAULT 0 COMMENT '用户id',
    `order_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '订单id',
    `goods_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '商品id',
    create_time        DATETIME       DEFAULT CURRENT_TIMESTAMP not null comment '创建时间',
    update_time        DATETIME       DEFAULT CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete          TINYINT        DEFAULT 0                 not null comment '是否删除',
    UNIQUE KEY `seckill_uid_gid` (`user_id`, `goods_id`) USING BTREE COMMENT ' 用户id，商品 id 的唯一索引，解决同一个用户多次抢购',
    PRIMARY KEY (`id`)
) ENGINE=INNODB AUTO_INCREMENT=300 DEFAULT CHARSET=utf8mb4;
