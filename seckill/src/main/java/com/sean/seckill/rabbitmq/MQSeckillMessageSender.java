package com.sean.seckill.rabbitmq;

import com.sean.seckill.config.RabbitMQConfig;
import com.sean.seckill.config.RabbitMQSeckillConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sean
 * @Date 2025/38/19
 * 秒杀消息发送者
 */
@Slf4j
@Service
public class MQSeckillMessageSender {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendSeckillMessage(String message) {
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend(RabbitMQSeckillConfig.EXCHANGE,"seckill.message", message);
    }
}
