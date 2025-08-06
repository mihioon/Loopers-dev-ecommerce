package com.loopers.domain.like;

import java.util.Map;
import java.util.Set;

public record LikeInfo(
        ProductLikeCount productLikeCount,
        UserLiked userLiked
) {
    public record ProductLikeCount(
            Map<Long, Long> likeCounts
    ) {
    }

    public record UserLiked(Set<Long> likedProductIds) {
        public boolean isLiked(Long productId) {
            return likedProductIds.contains(productId);
        }
    }
}
