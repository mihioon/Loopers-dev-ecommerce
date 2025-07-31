package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class LikeService {
    
    private final ProductLikeRepository productLikeRepository;

    @Transactional(readOnly = true)
    public Long getLikeCount(final Long productId) {
        return productLikeRepository.getLikeCount(productId);
    }

    @Transactional(readOnly = true)
    public LikeCountInfo getLikeCounts(final LikeCommand.GetLikeCount command) {
        Map<Long, Long> productLikeCounts = new HashMap<>();
        command.productIds().forEach(productId -> {
            productLikeCounts.put(productId, productLikeRepository.getLikeCount(productId));
        });

        return new LikeCountInfo(productLikeCounts);
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(final Long productId, final Long userId) {
        return productLikeRepository.isLikedByUser(productId, userId);
    }

    @Transactional(readOnly = true)
    public LikeCountInfo getLikedListByUser(final Long userId, final List<Long> productIds) {
        Map<Long, Long> productLikeCounts = new HashMap<>();
        productIds.forEach(productId -> {
            productLikeCounts.put(productId, productLikeRepository.isLikedByUser(productId, userId) ? 1L : 0L);
        });

        return new LikeCountInfo(productLikeCounts);
    }
}
