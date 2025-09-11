package com.loopers.events.product;

import com.loopers.events.common.DomainEvent;
import java.time.LocalDateTime;

public class ProductViewedEvent extends DomainEvent {
    private final Long productId;
    private final Long userId; // nullable - 비로그인 사용자도 있을 수 있음
    private final String sessionId; // 비로그인 사용자 추적용
    private final LocalDateTime viewedAt;
    private final String userAgent;
    private final String clientIp;

    public ProductViewedEvent(Long productId, Long userId, String sessionId) {
        super(String.valueOf(productId));
        this.productId = productId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.viewedAt = LocalDateTime.now();
        this.userAgent = null;
        this.clientIp = null;
    }

    public ProductViewedEvent(Long productId, Long userId, String sessionId, 
                             String userAgent, String clientIp) {
        super(String.valueOf(productId));
        this.productId = productId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.viewedAt = LocalDateTime.now();
        this.userAgent = userAgent;
        this.clientIp = clientIp;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getClientIp() {
        return clientIp;
    }

    @Override
    public String toString() {
        return String.format("ProductViewedEvent{productId=%d, userId=%s, sessionId='%s', viewedAt=%s}", productId, userId, sessionId, viewedAt);
    }
}
