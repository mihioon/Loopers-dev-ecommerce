package com.loopers.events.like;

import java.time.LocalDateTime;

public class LikeRemovedEvent extends LikeEvent {
    private final LocalDateTime unlikedAt;

    public LikeRemovedEvent(Long productId, Long userId) {
        super(productId, userId);
        this.unlikedAt = LocalDateTime.now();
    }

    public LocalDateTime getUnlikedAt() {
        return unlikedAt;
    }
}
