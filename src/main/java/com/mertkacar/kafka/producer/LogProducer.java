package com.mertkacar.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public void sendLog(String key, String message) {
        kafkaTemplate.send("logs", key, message);
    }
}
