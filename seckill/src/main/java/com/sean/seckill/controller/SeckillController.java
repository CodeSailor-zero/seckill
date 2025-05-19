package com.sean.seckill.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sean.seckill.common.ErrorCodeEnum;
import com.sean.seckill.exception.GlobalException;
import com.sean.seckill.model.dto.Order;
import com.sean.seckill.model.dto.SeckillOrder;
import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.vo.GoodsVo;
import com.sean.seckill.model.vo.SeckillMessage;
import com.sean.seckill.rabbitmq.MQSeckillMessageSender;
import com.sean.seckill.service.GoodsService;
import com.sean.seckill.service.OrderService;
import com.sean.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author sean
 * @Date 2025/18/18
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Resource
    private GoodsService goodsService;
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private OrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;

    //消息发送者
    @Resource
    private MQSeckillMessageSender mqSeckillMessageSender;

    //记录 商品是否还有库存
    //key为 商品的id
    //value为 true表示有库存，false表示无库存
    private HashMap<Long,Boolean> hasStock = new HashMap<>();

    @Deprecated
    @RequestMapping(value = "/doSeckill/v1")
    public String doSeckillV1(Model model, User user, Long goodsId) {
        System.out.println("-----秒杀 V1.0--------");
        //===================秒杀 v1.0 start =========================
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //解决重复抢购
        SeckillOrder seckillOrder =
                seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
                        .eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", ErrorCodeEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }

        //抢购
        Order order = orderService.seckill(user, goodsVo);
        if (order == null) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);

        return "orderDetail";
    }

    //===================秒杀 v2.0 start =========================
    //在service层，我们在redis存储了秒杀定单，利用redis快速过滤，提高效率
    //优化重复抢购：在抢购完成后，将 order:用户id:商品id 为key，seckillOrder为value存入redis
    //过滤直接查询redis，如果存在，则说明已经抢购过了【减少数据库操作】
    @Deprecated
    @RequestMapping(value = "/doSeckill/v2")
    public String doSeckillV2(Model model, User user, Long goodsId) {

        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //解决重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            // seckillOrder不为空，代表用户已经抢购过了。
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //抢购
        Order order = orderService.seckill(user, goodsVo);
        if (order == null) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);

        return "orderDetail";
    }

    //===================秒杀 v3.0 start =========================
    //预减库存
    //1. 通过 spring InitializingBean 接口实现 afterPropertiesSet() 方法，
    //来缓存秒杀订单的库存数
    //2. 在执行秒杀方法前，通过 redisTemplate.opsForValue().decrement() 方法进行库存预减
    //！！！decrement()具有原子性
    @Deprecated
    @RequestMapping(value = "/doSeckill/V3")
    public String doSeckillV3(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //解决重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            // seckillOrder不为空，代表用户已经抢购过了。
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //库存预减：从 reids 中获取商品的库存，使用 decrement() 方法进行减库存操作
        //！！！decrement()是具有原子性的
        //减少 执行 orderService.seckill(user, goodsVo) 方法的线程数，
        // 防止线程堆积，优化秒杀
        Long decrement = valueOperations.decrement("secKillGoods:" + goodsId);
        if (decrement < 0) {
            //为了数据好看，redis 中数据不为负数
            valueOperations.increment("secKillGoods:" + goodsId);
            //库存不足
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //抢购
        Order order = orderService.seckill(user, goodsVo);
        if (order == null) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);

        return "orderDetail";
    }

    @Deprecated
    @RequestMapping(value = "/doSeckill/V5")
    public String doSeckillV5(Model model, User user, Long goodsId) {
        //===================秒杀 v5.0 start =========================
        //预减库存
        //1. 通过 spring InitializingBean 接口实现 afterPropertiesSet() 方法，
        //来缓存秒杀订单的库存数
        //2. 在执行秒杀方法前，通过 redisTemplate.opsForValue().decrement() 方法进行库存预减
        //！！！decrement()具有原子性
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //解决重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            // seckillOrder不为空，代表用户已经抢购过了。
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        //从 hasStock 中获取商品库存状态，查看是否还要库存，如果没有则返回
        // true表示有库存，false表示无库存
        Boolean isHasStock = hasStock.get(goodsId);
        if (!isHasStock) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }


        //库存预减：从 reids 中获取商品的库存，使用 decrement() 方法进行减库存操作
        //！！！decrement()是具有原子性的
        //减少 执行 orderService.seckill(user, goodsVo) 方法的线程数，
        // 防止线程堆积，优化秒杀
        Long decrement = valueOperations.decrement("secKillGoods:" + goodsId);
        if (decrement < 0) {

            // 修改 hasStock 的商品库存状态
            // true表示有库存，false表示无库存
            hasStock.put(goodsId, false);

            //为了数据好看，redis 中数据不为负数
            valueOperations.increment("secKillGoods:" + goodsId);
            //库存不足
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        //抢购
        Order order = orderService.seckill(user, goodsVo);
        if (order == null) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);

        return "orderDetail";
    }

    @RequestMapping(value = "/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        //===================秒杀 v5.0 start =========================

        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //解决重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            // seckillOrder不为空，代表用户已经抢购过了。
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }

        //从 hasStock 中获取商品库存状态，查看是否还要库存，如果没有则返回
        // true表示有库存，false表示无库存
        Boolean isHasStock = hasStock.get(goodsId);
        if (!isHasStock) {
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }


        //库存预减：从 reids 中获取商品的库存，使用 decrement() 方法进行减库存操作
        //！！！decrement()是具有原子性的
        //减少 执行 orderService.seckill(user, goodsVo) 方法的线程数，
        // 防止线程堆积，优化秒杀
        Long decrement = valueOperations.decrement("secKillGoods:" + goodsId);
        if (decrement < 0) {

            // 修改 hasStock 的商品库存状态
            // true表示有库存，false表示无库存
            hasStock.put(goodsId, false);

            //为了数据好看，redis 中数据不为负数
            valueOperations.increment("secKillGoods:" + goodsId);
            //库存不足
            model.addAttribute("errmsg", ErrorCodeEnum.ENTRY_STOCK.getMessage());
            return "secKillFail";
        }
        SeckillMessage seckillMessage = SeckillMessage.builder()
                .user(user)
                .goodsId(goodsId)
                .build();
        String seckillMessageStr = JSONUtil.toJsonStr(seckillMessage);
        mqSeckillMessageSender.sendSeckillMessage(seckillMessageStr);
        model.addAttribute("errmsg", "排队中.....");
        return "secKillFail";
    }

    /**
     * 缓存商品库存，用于预减库存使用
     * 【Spring 容器会在该 Bean 的所有属性被设置之后调用】
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(goodsVo)) {
            throw new GlobalException(ErrorCodeEnum.PARAM_ERROR);
        }
        //将商品库存存入到redis，以 secKillGoods:商品id 为key
        goodsVo.forEach(goods -> {
            redisTemplate.opsForValue()
                    .set("secKillGoods:" + goods.getId(), goods.getStockCount());
            //初始化 hasStock
            // true表示有库存，false表示无库存
            hasStock.put(goods.getId(),goods.getStockCount() > 0);
        });
    }
}
