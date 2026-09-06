package com.letsplay.api.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * TokenBlacklistService
 */
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Autowired
    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(String userId) {
        return "revoked:" + userId;
    }

    public void revokeAllTokensForUser(String userId) {
        long nowEpochSeconds = Instant.now().getEpochSecond();
        redisTemplate.opsForValue().set(key(userId), String.valueOf(nowEpochSeconds), Duration.ofMillis(expirationMs));
    }

}