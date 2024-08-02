package com.softeer.podoarrival.event.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedissonClient redissonClient;

    public void setValues(String key, String data) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(data);
    }

    public void setValues(String key, String data, Duration duration) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(data, duration);
    }

    public String getValues(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.isExists() ? bucket.get() : null;
    }

    public void deleteValues(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    public void expireValues(String key, int timeout) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.expire(Duration.ofMillis(timeout));
    }

    public void setHashOps(String key, Map<String, String> data) {
        RMap<String, String> map = redissonClient.getMap(key);
        map.putAll(data);
    }

    public String getHashOps(String key, String hashKey) {
        RMap<String, String> map = redissonClient.getMap(key);
        return map.containsKey(hashKey) ? map.get(hashKey) : "";
    }

    public void deleteHashOps(String key, String hashKey) {
        RMap<String, String> map = redissonClient.getMap(key);
        map.remove(hashKey);
    }
}
