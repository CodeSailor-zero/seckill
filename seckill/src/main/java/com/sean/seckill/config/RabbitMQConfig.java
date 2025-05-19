package com.sean.seckill.config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sean
 * @Date 2025/33/19
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "seckill_queue";

    /**
     * 1. 配置队列
     * 2. 队列名为 seckill_queue
     * 3. true 表示: 持久化
     * durable： 队列是否持久化。 队列默认是存放到内存中的，rabbitmq 重启则丢失，
     * 若想重启之后还存在则队列要持久化，保存到 Erlang 自带的 Mnesia 数据库中，当
     * rabbitmq 重启之后会读取该数据库
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }
}
