package com.sean.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sean.seckill.mapper.OrderMapper;
import com.sean.seckill.mapper.SeckillGoodsMapper;
import com.sean.seckill.model.dto.Order;
import com.sean.seckill.model.dto.SeckillGoods;
import com.sean.seckill.model.dto.SeckillOrder;
import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.vo.GoodsVo;
import com.sean.seckill.service.OrderService;
import com.sean.seckill.service.SeckillGoodsService;
import com.sean.seckill.service.SeckillOrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author sean
 * @Date 2025/55/18
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {
    @Resource
    private SeckillGoodsService seckillGoodsService;

    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;

    @Resource
    private OrderService orderService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 完成秒杀功能
     *
     * @param user    用户信息
     * @param goodsVo 商品信息
     * @return 订单信息
     */
    @Transactional //事务注解
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        //获取当前秒杀商品的信息
        Long goodsVoId = goodsVo.getId();

        QueryWrapper<SeckillGoods> seckillGoodsQueryWrapper = new QueryWrapper<>();
        seckillGoodsQueryWrapper.eq("goods_id", goodsVoId);
        SeckillGoods seckillGoods = seckillGoodsService.getOne(seckillGoodsQueryWrapper);

        //查询当前的库存，并且减一
        //这里不具备原子性，只是简单实现，后续改进
//        Integer newStockCount = seckillGoods.getStockCount() - 1;
//        seckillGoods.setStockCount(newStockCount);
//        seckillGoodsService.updateById(seckillGoods);

        //1. Mysql在默认的事务隔离级别[可重复读]下
        //2.执行update语句时，会在事务中锁定要更新的行（行锁）
        //3.这样可以防止其他事务在同一行执行update、delete
        int newStockCount = seckillGoods.getStockCount() - 1;
        UpdateWrapper<SeckillGoods> seckillGoodsUpdateWrapper = new UpdateWrapper<>();
        //setSql("stock_count = stock_count - 1") 是直接将数据减少，再更新到数据库
        //set("stock_count", newStockCount) 是先查询数据库中的值，再减少，然后更新到数据库
        seckillGoodsUpdateWrapper.setSql("stock_count = stock_count - 1")
                .eq("goods_id", goodsVoId)
                .gt("stock_count", 0);
        boolean update = seckillGoodsService.update(seckillGoodsUpdateWrapper);
        if (!update) {
            //如果更新失败，说明已经没有库存了
            return null;
        }
        System.out.println("库存减少完成：" + newStockCount);
        Long userId = user.getId();

        //保存到Order表中
        Order order = new Order();
        order.setUserId(userId);
        order.setGoodsId(goodsVoId);
        order.setDeliveryAddrId(1L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(seckillGoods.getStockCount());
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(0);
        order.setStatus(0); // 订单状态：0 新建未支付1已支付2 已发货 3 已收货 4 已退款 5 已完成
        orderService.save(order);

        //保存到SeckillOrder表中
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(userId);
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVoId);
        seckillOrderService.save(seckillOrder);

        //将秒杀订单保存到redis，优化controller层进行过滤
        //设计秒杀订单的key =》 order:用户id:商品id
        String seckillOrderKey = "order:" + userId + ":" + goodsVoId;
        redisTemplate.opsForValue()
                .set(seckillOrderKey, seckillOrder);

        return order;
    }
}
