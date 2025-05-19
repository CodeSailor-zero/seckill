package com.sean.seckill.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author sean
 * @Date 2025/26/18
 */
@Data
@TableName("orders")
public class Order implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 收货地址id
     */
    private Long deliveryAddrId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品下单数量
     */
    private Integer goodsCount;

    /**
     * 付款金额
     */
    private BigDecimal goodsPrice;

    /**
     * 订单渠道 1pc，2Android，3ios
     */
    private Integer orderChannel;

    /**
     * 订单状态：0 新建未支付 1 已支付 2 已发货 3 已收货 4 已退款5 已完成*/
    private Integer status;

    /**
     * 订单创建时间
     */
    private Date createDate;

    /**
     * 订单支付时间
     */
    private Date payDate;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
    private static final long serialVersionUID = 5667980L;
}
