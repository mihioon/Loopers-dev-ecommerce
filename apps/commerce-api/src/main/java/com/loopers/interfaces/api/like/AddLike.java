package com.loopers.interfaces.api.like;

public class AddLike {
    public record V1() {
        public record Response(
                Long productId,
                Long likeCount,
                Boolean isLiked
        ) {
            public static Response of(Long productId, Long likeCount) {
                return new Response(productId, likeCount, true);
            }
        }
    }
}
