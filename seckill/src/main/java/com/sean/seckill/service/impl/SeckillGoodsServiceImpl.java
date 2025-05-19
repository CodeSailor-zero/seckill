package com.sean.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sean.seckill.mapper.SeckillGoodsMapper;
import com.sean.seckill.model.dto.SeckillGoods;
import com.sean.seckill.service.SeckillGoodsService;
import org.springframework.stereotype.Service;

/**
 * @author sean
 * @Date 2025/55/18
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
        implements SeckillGoodsService {
}
