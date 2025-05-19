package com.sean.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sean.seckill.mapper.SeckillOrderMapper;
import com.sean.seckill.model.dto.SeckillOrder;
import com.sean.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

/**
 * @author sean
 * @Date 2025/55/18
 */
@Service
public class SecckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
        implements SeckillOrderService {
}
