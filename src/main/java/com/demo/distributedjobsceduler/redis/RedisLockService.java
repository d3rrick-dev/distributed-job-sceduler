package com.demo.distributedjobsceduler.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final RedisTemplate<String, String> redisTemplate;

    public String tryLock(String key, long expireMillis) {
        var token = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, token, Duration.ofMillis(expireMillis));
        if (Boolean.TRUE.equals(success)) {
            return token;
        }
        return null;
    }

    public boolean releaseLock(String key, String token) {
        var currentValue = redisTemplate.opsForValue().get(key);
        if (token.equals(currentValue)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}
