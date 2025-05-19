package com.sean.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sean.seckill.common.ErrorCodeEnum;
import com.sean.seckill.exception.GlobalException;
import com.sean.seckill.mapper.GoodsMapper;
import com.sean.seckill.model.dto.Goods;
import com.sean.seckill.model.vo.GoodsVo;
import com.sean.seckill.service.GoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sean
 * @Date 2025/51/18
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 返回封装的GoodsVo的商品列表
     * @return 返回封装的GoodsVo的商品列表
     */
    @Override
    public List<GoodsVo> findGoodsVo() {
        return goodsMapper.findGoodsVoList();
    }

    /**
     * 根据商品 id 来获取商品详细信息
     * @param goodsId 商品的id
     * @return 返回封装的GoodsVo的商品对象
     */
    @Override
    public GoodsVo findGoodsVoByGoodsId(Long goodsId) {
        if (goodsId == null) {
            throw new GlobalException(ErrorCodeEnum.BING_ERROR);
        }
        return goodsMapper.findGoodsVOByGoodsId(goodsId);
    }
}
