package com.mertkacar.controller;


import com.mertkacar.kafka.producer.LogProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Slf4j
public class LogController {

    private final LogProducer logProducer;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/sendLog")
    public String createLog(@RequestBody String message) {
        logProducer.sendLog(message , "message");
        log.info("Log gönderildi: " + message);
        return "log gönderildi " + message;
    }

    @GetMapping("/getlogs")
    public List<String> getLogs () {
        //Son on logu çekmek için range kullandım.
        return redisTemplate.opsForList().range("logs", 0, 9);

    }
}
