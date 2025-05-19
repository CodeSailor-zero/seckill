package com.sean.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sean.seckill.model.dto.Order;
import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.vo.GoodsVo;

/**
 * @author sean
 * @Date 2025/45/18
 */
public interface OrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goodsVo);
}
