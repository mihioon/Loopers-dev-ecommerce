package com.loopers.domain.payment.event;

import com.loopers.domain.common.event.DomainEvent;
import java.math.BigDecimal;

public abstract class PaymentEvent extends DomainEvent {
    private final String paymentId;
    private final Long orderId;
    private final BigDecimal amount;

    protected PaymentEvent(String paymentId, Long orderId, BigDecimal amount) {
        super(paymentId);
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
