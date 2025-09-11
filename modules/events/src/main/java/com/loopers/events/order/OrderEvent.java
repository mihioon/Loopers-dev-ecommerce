package com.loopers.events.order;

import com.loopers.events.common.DomainEvent;

public abstract class OrderEvent extends DomainEvent {
    private final Long orderId;
    private final Long userId;

    protected OrderEvent(Long orderId, Long userId) {
        super(String.valueOf(orderId));
        this.orderId = orderId;
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }
}
