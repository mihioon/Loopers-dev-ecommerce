package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentInfo {
    
    public record Detail(
            Long id,
            Long orderId,
            Long userId,
            BigDecimal amount,
            BigDecimal pointAmount,
            Payment.PaymentStatus status
    ) {
        public static Detail from(Payment payment) {
            return new Detail(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getAmount(),
                    payment.getPointAmount(),
                    payment.getStatus()
            );
        }
    }
}