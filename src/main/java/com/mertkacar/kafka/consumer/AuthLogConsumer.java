package com.mertkacar.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertkacar.dtos.events.AuthEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthLogConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "auth-events", groupId = "audit-consumers-v2") // yeni grup → eski offsetleri atla
    public void consumer(String message) {
        try {
            AuthEvent event = mapper.readValue(message, AuthEvent.class);

            // timeline list
            String listKey = "audit:auth";
            redisTemplate.opsForList().leftPush(listKey, event);
            Long size = redisTemplate.opsForList().size(listKey);
            if (size != null && size > 1000) {
                redisTemplate.opsForList().trim(listKey, 0, 999);
            }

            // kv
            String kvKey = "auth:" + event.getType().toLowerCase() + ":" + event.getTimestamp();
            redisTemplate.opsForValue().set(kvKey, event);

            System.out.println("Redis'e yazıldı -> " + listKey + " & " + kvKey);
        } catch (Exception e) {
            // Fallback: JSON parse edilemezse raw mesajı da kaybetme
            String rawKey = "auth:raw:" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(rawKey, message);
            System.err.println("AuthEvent parse hatası: " + e.getMessage() + " | raw saved as " + rawKey);
        }
    }
}