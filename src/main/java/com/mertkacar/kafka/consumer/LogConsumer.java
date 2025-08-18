package com.mertkacar.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogConsumer {
 private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "app-logs", groupId = "log-consumers")
    public void consumeLog(String message) {
        System.out.println("Kafka’dan log alındı: " + message);
        redisTemplate.opsForList().leftPush("logs", message);
    }
}
