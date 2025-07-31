package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeResult;

import java.util.List;

public class GetUserLikeProducts {
    public record V1() {
        public record Response(
                List<Long> products
        ) {
            public static Response of(LikeResult result) {
                return new Response(
                        result.productIds().stream()
                                .toList()
                );
            }
        }
    }
}
