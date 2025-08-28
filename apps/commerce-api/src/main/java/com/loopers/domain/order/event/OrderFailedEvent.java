package com.loopers.domain.order.event;

public class OrderFailedEvent extends OrderEvent {
    private final String failureReason;
    private final String errorCode;

    public OrderFailedEvent(Long orderId, Long userId, String failureReason, String errorCode) {
        super(orderId, userId);
        this.failureReason = failureReason;
        this.errorCode = errorCode;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
