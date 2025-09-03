package com.loopers.domain.like.event;

import com.loopers.domain.common.event.DomainEvent;

public abstract class LikeEvent extends DomainEvent {
    private final Long productId;
    private final Long userId;

    protected LikeEvent(Long productId, Long userId) {
        super(String.format("%d:%d", productId, userId));
        this.productId = productId;
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getUserId() {
        return userId;
    }
}