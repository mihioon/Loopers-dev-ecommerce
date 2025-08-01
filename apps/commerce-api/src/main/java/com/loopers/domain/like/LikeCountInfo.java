package com.loopers.domain.like;

import java.util.Map;

public record LikeCountInfo(
    Map<Long, Long> likeCounts
) {
    // productId, likeCount

}
