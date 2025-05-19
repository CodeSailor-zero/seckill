package com.sean.seckill.model.vo;

import com.sean.seckill.model.dto.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sean
 * @Date 2025/30/19
 * 秒杀消息的封装（rabbitmq）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodsId;
}
