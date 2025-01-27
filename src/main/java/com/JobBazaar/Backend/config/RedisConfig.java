package com.JobBazaar.Backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisConfig {
    @Value("${redis.redisHost}")
    private String REDIS_HOST;

    @Value("${redis.redisPassword}")
    private String REDIS_PASSWORD;

    public Jedis connect() {
        Jedis jedis = new Jedis(REDIS_HOST, 6379, true);
        jedis.auth(REDIS_PASSWORD);
        return jedis;
    }
}
