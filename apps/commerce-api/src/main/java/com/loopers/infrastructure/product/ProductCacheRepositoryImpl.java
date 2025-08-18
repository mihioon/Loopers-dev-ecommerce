package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCacheRepository;
import com.loopers.support.cache.RedisCacheRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ProductCacheRepositoryImpl implements ProductCacheRepository {

    private final RedisCacheRepositoryImpl<Object> cacheRepository;

    @Override
    public Long get(String key) {
        try {
            Object value = cacheRepository.get(key);
            if (value == null) return null;
            return Long.valueOf(value.toString());
        } catch (Exception e) {
            // TODO: 로깅
            return null;
        }
    }

    @Override
    public void set(String key, Long count, Duration ttl) {
        try {
            cacheRepository.set(key, String.valueOf(count), ttl);
        } catch (Exception e) {
            // TODO: 로깅
            return;
        }
    }

    @Override
    public void delete(String key) {
        try {
            cacheRepository.delete(key);
        } catch (Exception e) {
            // TODO: 로깅
            return;
        }
    }
}
