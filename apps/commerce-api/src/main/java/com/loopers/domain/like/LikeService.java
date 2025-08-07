package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Component
public class LikeService {
    private final LikeTransactionHelper likeTransactionHelper;
    private final ProductLikeRepository productLikeRepository;

    @Transactional
    public void like(final LikeCommand.Like command) {
        try {
            likeTransactionHelper.saveLike(command);
            updateLikeCount(command.productId(), true);
        } catch (DataIntegrityViolationException e) {
            return;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlike(final LikeCommand.Unlike command) {
        if (deleteIfExists(command.productId(), command.userId())) {
            updateLikeCount(command.productId(), false);
        }
    }

    public boolean deleteIfExists(Long productId, Long userId) {
        return productLikeRepository.deleteByProductIdAndUserId(productId, userId) > 0;
    }
    
    private void updateLikeCount(Long productId, boolean increment) {
        ProductLikeCount likeCount = productLikeRepository.findLikeCountByProductId(productId)
                .orElse(new ProductLikeCount(productId));
        
        if (increment) {
            likeCount.increase();
        } else {
            likeCount.decrease();
        }
        
        productLikeRepository.save(likeCount);
    }

    @Transactional(readOnly = true)
    public Long getLikeCount(final Long productId) {
        return productLikeRepository.findLikeCountByProductId(productId)
                .map(ProductLikeCount::getLikeCount)
                .map(Long::valueOf)
                .orElse(0L);
    }

    @Transactional(readOnly = true)
    public LikeInfo getLikeCounts(final Long userId, final LikeCommand.GetLikeCount command) {
        Map<Long, Long> productLikeCounts = productLikeRepository.getLikeCountsFromCountTable(command.productIds());

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
