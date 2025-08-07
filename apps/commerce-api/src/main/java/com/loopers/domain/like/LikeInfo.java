package com.loopers.domain.like;

import java.util.Set;

public record LikeInfo(
        UserLiked userLiked
) {
    public record UserLiked(Set<Long> likedProductIds) {
        public boolean isLiked(Long productId) {
            return likedProductIds.contains(productId);
        }
    }
}
