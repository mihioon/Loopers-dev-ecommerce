package com.loopers.domain.like;

import com.loopers.domain.product.dto.ProductInfo;

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
        public static GetLikeCount from(ProductInfo.Summary summary) {
            return new GetLikeCount(summary.products().stream()
                    .map(ProductInfo.Summary.Item::id)
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
        public static GetLikedByUser from(ProductInfo.Summary summary, Long userId) {
            return new GetLikedByUser(summary.products().stream()
                    .map(ProductInfo.Summary.Item::id)
                    .toList(), userId);
        }
    }
}
