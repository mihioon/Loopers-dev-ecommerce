package com.loopers.domain.payment.event;

import java.math.BigDecimal;

public class PaymentFailedEvent extends PaymentEvent {
    private final String failureReason;
    private final String errorCode;

    public PaymentFailedEvent(String paymentId, Long orderId, BigDecimal amount,
                             String failureReason, String errorCode) {
        super(paymentId, orderId, amount);
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
