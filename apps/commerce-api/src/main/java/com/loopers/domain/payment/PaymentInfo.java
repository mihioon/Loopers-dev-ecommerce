package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentInfo {
    
    public record Detail(
            Long id,
            Long userId,
            BigDecimal amount,
            Payment.PaymentStatus status
    ) {
        public static Detail from(Payment payment) {
            return new Detail(
                    payment.getId(),
                    payment.getUserId(),
                    payment.getAmount(),
                    payment.getStatus()
            );
        }
    }
}
