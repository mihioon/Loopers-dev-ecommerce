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
        Object value = cacheRepository.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public void set(String key, Long count, Duration ttl) {
        cacheRepository.set(key, String.valueOf(count), ttl);
    }

    @Override
    public void delete(String key) {
        cacheRepository.delete(key);
    }
}
