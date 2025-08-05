package com.loopers.interfaces.api.like;

public class RemoveLike {
    public record V1() {
        public record Response(
                Long productId,
                Long likeCount,
                Boolean isLiked
        ) {
            public static RemoveLike.V1.Response of(Long productId, Long likeCount) {
                return new RemoveLike.V1.Response(productId, likeCount, false);
            }
        }
    }
}
