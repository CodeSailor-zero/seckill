package com.sean.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sean.seckill.model.dto.Goods;
import com.sean.seckill.model.vo.GoodsVo;

import java.util.List;

/**
 * @author sean
 * @Date 2025/30/18
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    // 获取商品列表 -秒杀
    List<GoodsVo> findGoodsVoList();

    // 获取商品的详细信息
    GoodsVo findGoodsVOByGoodsId(Long goodsId);
}
