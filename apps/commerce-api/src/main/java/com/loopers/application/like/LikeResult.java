package com.loopers.application.like;

import com.loopers.domain.like.UserLikeProductInfo;

import java.util.List;

public record LikeResult(
        List<Long> productIds
){
    public static LikeResult from(UserLikeProductInfo likeCountInfo) {
        return new LikeResult(
                likeCountInfo.productIds().stream()
                        .toList()
        );
    }
}
