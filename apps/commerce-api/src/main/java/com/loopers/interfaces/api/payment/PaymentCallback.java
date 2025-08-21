package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentCriteria;

import java.math.BigDecimal;

public class PaymentCallback {
    public record V1() {
        public record Request(
                String transactionKey,
                String orderId,
                String cardType,
                String cardNo,
                BigDecimal amount,
                String status,
                String reason
        ) {
            public PaymentCriteria.Callback toCriteria() {
                return new PaymentCriteria.Callback(
                        transactionKey,
                        orderId,
                        cardType,
                        cardNo,
                        amount,
                        status,
                        reason
                );
            }
        }

        public record Response(
                String message
        ) {}
    }
}
