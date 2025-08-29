package com.loopers.domain.like;

import com.loopers.domain.common.event.EventPublisher;
import com.loopers.domain.like.event.LikeAddedEvent;
import com.loopers.domain.like.event.LikeRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Component
public class LikeService {
    private final ProductLikeRepository productLikeRepository;
    private final EventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void like(final LikeCommand.Like command) {
        productLikeRepository.save(new ProductLike(command.productId(), command.userId()));
        
        // 이벤트 발행
        eventPublisher.publish(
            new LikeAddedEvent(command.productId(), command.userId())
        );
    }

    @Transactional
    public boolean unlike(final LikeCommand.Unlike command) {
        int deleted = productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId());
        
        if (deleted > 0) {
            // 이벤트 발행
            eventPublisher.publish(
                new LikeRemovedEvent(command.productId(), command.userId())
            );
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public LikeInfo getUserLikedProducts(final Long userId, final LikeCommand.GetLikeCount command) {
        Set<Long> likedProductIds = (userId != null)
                ? productLikeRepository.getLikedProductIds(userId, command.productIds())
                : Set.of();

        return new LikeInfo(
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
