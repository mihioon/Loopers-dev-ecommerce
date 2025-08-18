package com.loopers.support.cache;

import java.time.Duration;

public interface CacheRepository<T> {
    
    T get(String key);
    
    void set(String key, T value, Duration ttl);
    
    void delete(String key);
}
