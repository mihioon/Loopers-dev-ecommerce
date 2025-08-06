package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Component
public class LikeService {
    private final LikeTransactionHelper likeTransactionHelper;
    private final ProductLikeRepository productLikeRepository;

    public void like(final LikeCommand.Like command) {
        try {
            likeTransactionHelper.saveLike(command);
        } catch (DataIntegrityViolationException e) {
            return;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlike(final LikeCommand.Unlike command) {
        productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId());
    }

    @Transactional(readOnly = true)
    public Long getLikeCount(final Long productId) {
        return productLikeRepository.getLikeCount(productId);
    }

    @Transactional(readOnly = true)
    public LikeInfo getLikeCounts(final Long userId, final LikeCommand.GetLikeCount command) {
        Map<Long, Long> productLikeCounts = productLikeRepository.getLikeCounts(command.productIds());

        Set<Long> likedProductIds = (userId != null)
                ? productLikeRepository.getLikedProductIds(userId, command.productIds())
                : Set.of();

        return new LikeInfo(
                new LikeInfo.ProductLikeCount(productLikeCounts),
                new LikeInfo.UserLiked(likedProductIds)
        );
    }

    @Transactional(readOnly = true)
    public boolean isLikedByUser(final Long productId, final Long userId) {
        return productLikeRepository.isLikedByUser(productId, userId);
    }

    @Transactional(readOnly = true)
    public UserLikeProductInfo getUserLikeProducts(final Long userId) {
        return UserLikeProductInfo.from(productLikeRepository.findByUserId(userId));
    }
}
