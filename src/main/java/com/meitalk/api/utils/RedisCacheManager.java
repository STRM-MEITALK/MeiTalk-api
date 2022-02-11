package com.meitalk.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meitalk.api.model.stream.enums.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class RedisCacheManager {

    private final HashOperations<String, Object, Object> hashOps;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
    }

    public <T> T getHashType(RedisKey key, String hashKey, Class<T> returnType) {
        try {
            String cached = (String) hashOps.get(key.name(), hashKey);
            return cached != null ? objectMapper.readValue(cached, returnType) : null;
        } catch (Exception e) {
            log.warn("get hash type fail => {}, {} :::: {}", key, hashKey, e.getMessage());
            return null;
        }
    }

    public <T> void setHashType(RedisKey key, String hashKey, T data) {
        try {
            String value = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
            hashOps.put(key.name(), hashKey, value);
        } catch (Exception e) {
            log.warn("set hash type fail => {}, {} :::: {}", key, hashKey, e.getMessage());
        }
    }

    public <T> void setHashType(RedisKey key, String hashKey, T data, int minute) {
        try {
            String value = data instanceof String ? (String) data : objectMapper.writeValueAsString(data);
            hashOps.put(key.name(), hashKey, value);
        } catch (Exception e) {
            log.warn("set hash type fail => {}, {} :::: {}", key, hashKey, e.getMessage());
        }
    }

    public  <T> List<T> convertToList(String data, Class<T> returnType) {
        try {
            return objectMapper.readValue(data, new TypeReference<List<T>>(){});
        } catch (JsonProcessingException e) {
            log.warn("convert list fail => {}", e.getMessage());
        }
        return null;
    }

    public String convertToJsonString(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.warn("json processing fail => {}", e.getMessage());
        }
        return null;
    }

    public void removeHashType(RedisKey key, String hashKey) {
        try {
            hashOps.delete(key.name(), hashKey);
        } catch (Exception e) {
            log.warn("remove cache list fail => {}, {} :::: {}", key, hashKey, e.getMessage());
        }
    }

    public void removeHashType(RedisKey key) {
        try {
            Set<Object> keys = hashOps.keys(key.name());
            keys.forEach(k -> hashOps.delete(key.name(), k));
        } catch (Exception e) {
            log.warn("remove cache list fail => {} :::: {}", key, e.getMessage());
        }
    }

    public void removeAllList() {
        try {
            RedisKey[] values = RedisKey.values();
            for (RedisKey value : values) {
                if (value.name().endsWith("LIST")) {
                    removeHashType(value);
                }
            }
        } catch (Exception e) {
            log.warn("remove all cache list fail => {}", e.getMessage());
        }
    }

    public String listCacheRedisHashKey(String type, int pageNo, int pageSize) {
        return type + "_" + pageNo + "_" + pageSize;
    }

    public String listCacheRedisHashKey(Long channelId, String type, Integer pageNo, Integer pageSize) {
        return channelId + "_" + type + "_" + pageNo + "_" + pageSize;
    }
}
