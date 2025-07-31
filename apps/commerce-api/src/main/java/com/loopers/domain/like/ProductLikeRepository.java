package com.loopers.domain.like;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductLikeRepository {
    ProductLike save(ProductLike productLike);

    Optional<ProductLike> findById(Long id);

    Long getLikeCount(Long productIds);

    Map<Long, Long> getLikeCounts(List<Long> productIds);

    boolean isLikedByUser(Long productIds, Long userId);
}
