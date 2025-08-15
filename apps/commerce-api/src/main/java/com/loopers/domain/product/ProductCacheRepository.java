package com.loopers.domain.product;

import java.time.Duration;

public interface ProductCacheRepository {

    Long get(String key);

    void set(String key, Long count, Duration ttl);

    void delete(String key);
}
