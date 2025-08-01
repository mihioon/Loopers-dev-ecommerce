package com.loopers.domain.like;

import java.util.List;

public record UserLikeProductInfo(
        List<Long> productIds
) {
    public static UserLikeProductInfo from(List<ProductLike> productLikes) {
        return new UserLikeProductInfo(
                productLikes.stream()
                        .map(ProductLike::getProductId)
                        .toList()
        );
    }
}
