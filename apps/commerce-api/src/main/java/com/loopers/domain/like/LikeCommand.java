package com.loopers.domain.like;

import com.loopers.domain.catalog.product.ProductInfo;

import java.util.List;

public class LikeCommand {
    public record Like(
            Long productId,
            Long userId
    ) {
    }

    public record Unlike(
            Long productId,
            Long userId
    ) {
    }

    public record GetLikeCount(
            List<Long> productIds
    ) {
        public static GetLikeCount from(ProductInfo.Summery summery) {
            return new GetLikeCount(summery.products().stream()
                    .map(ProductInfo.Summery.Item::id)
                    .toList());
        }

        public static GetLikeCount from(Long productId) {
            return new GetLikeCount(List.of(productId));
        }
    }

    public record GetLikedByUser(
            List<Long> productIds,
            Long userId
    ) {
        public static GetLikedByUser from(ProductInfo.Summery summery, Long userId) {
            return new GetLikedByUser(summery.products().stream()
                    .map(ProductInfo.Summery.Item::id)
                    .toList(), userId);
        }
    }
}
