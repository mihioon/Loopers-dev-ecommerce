package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentCriteria;

import java.math.BigDecimal;

public class ProcessPayment {
    public record V1() {
        public record Request(
                BigDecimal amount,
                Long orderId,
                String cardType,
                String cardNo
        ) {
            public PaymentCriteria.Process toCriteria(Long userId) {
                return new PaymentCriteria.Process(
                        userId,
                        amount,
                        orderId,
                        cardType,
                        cardNo
                );
            }
        }

        public record Response(
                String paymentId
        ) {}
    }
}
