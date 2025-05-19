package com.sean.seckill.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.sean.seckill.config.RabbitMQConfig;
import com.sean.seckill.config.RabbitMQSeckillConfig;
import com.sean.seckill.model.dto.User;
import com.sean.seckill.model.vo.GoodsVo;
import com.sean.seckill.model.vo.SeckillMessage;
import com.sean.seckill.service.GoodsService;
import com.sean.seckill.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sean
 * @Date 2025/41/19
 * 秒杀消息的接收者
 */
@Slf4j
@Service
public class MQSeckillMessageReciver {
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;

    @RabbitListener(queues = RabbitMQSeckillConfig.QUEUE)
    public void receive(String message){
        log.info("接收消息：" + message);
        SeckillMessage seckillMessage = JSONUtil.toBean(message, SeckillMessage.class);
        //下单用户消息
        User user = seckillMessage.getUser();
        //商品id
        long goodsId = seckillMessage.getGoodsId();
        //商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        //异步进行下单操作
        orderService.seckill(user, goodsVo);
    }
}
