package com.sean.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sean
 * @Date 2025/35/19
 * 秒杀消息队列配置
 */
@Configuration
public class RabbitMQSeckillConfig {
    public static final String QUEUE = "seckillQueue";
    public static final String EXCHANGE = "seckillExchange";

    @Bean
    public Queue seckillQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public TopicExchange seckillExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Binding bindingSeckillQueue() {
        return BindingBuilder.bind(seckillQueue())
                .to(seckillExchange())
                .with("seckill.#");
    }
}
