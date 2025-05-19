package com.sean.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sean.seckill.model.dto.Goods;
import com.sean.seckill.model.vo.GoodsVo;

import java.util.List;

/**
 * @author sean
 * @Date 2025/50/18
 */
public interface GoodsService extends IService<Goods> {
    /**
     * 返回封装的GoodsVo的商品列表
     * @return 返回封装的GoodsVo的商品列表
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 根据商品 id 来获取商品详细信息
     * @param goodsId 商品的id
     * @return 返回封装的GoodsVo的商品对象
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
