package com.loopers.events.like;

import java.time.LocalDateTime;

public class LikeAddedEvent extends LikeEvent {
    private final LocalDateTime likedAt;

    public LikeAddedEvent(Long productId, Long userId) {
        super(productId, userId);
        this.likedAt = LocalDateTime.now();
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }
}
