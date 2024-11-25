package com.shopping.ecommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.core.type.TypeReference;


@Service
@Slf4j
public class RedisService {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public <T> T get(String key, TypeReference<T> typeReference) {
        try {
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null) {
                log.warn("No value found for key: {}", key);
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(o.toString(), typeReference);
        } catch (Exception e) {
            log.error("Error retrieving key {}: {}", key, e.getMessage(), e);
            return null;
        }
    }

    public void set(String key, Object o, Long ttl) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonValue = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error setting key {}: {}", key, e.getMessage(), e);
        }
    }
}