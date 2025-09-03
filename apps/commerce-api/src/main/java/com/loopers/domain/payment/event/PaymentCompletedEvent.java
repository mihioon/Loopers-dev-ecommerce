package com.loopers.domain.payment.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentCompletedEvent extends PaymentEvent {
    private final String transactionId;
    private final LocalDateTime paidAt;

    public PaymentCompletedEvent(String paymentId, Long orderId, BigDecimal amount,
                                String transactionId, LocalDateTime paidAt) {
        super(paymentId, orderId, amount);
        this.transactionId = transactionId;
        this.paidAt = paidAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}