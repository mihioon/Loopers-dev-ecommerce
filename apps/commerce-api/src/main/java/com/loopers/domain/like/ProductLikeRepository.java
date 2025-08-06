package com.loopers.domain.like;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ProductLikeRepository {

    ProductLike save(ProductLike productLike);

    void deleteByProductIdAndUserId(Long productId, Long userId);

    Optional<ProductLike> findById(Long id);

    Long getLikeCount(Long productIds);

    List<ProductLike> findLikeCounts(List<Long> productIds);

    Map<Long, Long> getLikeCounts(List<Long> productIds);

    Set<Long> getLikedProductIds(Long userId, List<Long> productIds);

    boolean isLikedByUser(Long productIds, Long userId);

    List<ProductLike> findByUserId(Long userId);
}
