package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentInfo {
    
    public record Detail(
            String paymentId,
            Long userId,
            BigDecimal amount,
            Payment.PaymentStatus status
    ) {
        public static Detail from(Payment payment) {
            return new Detail(
                    payment.getPaymentId(),
                    payment.getUserId(),
                    payment.getAmount(),
                    payment.getStatus()
            );
        }
    }
}
