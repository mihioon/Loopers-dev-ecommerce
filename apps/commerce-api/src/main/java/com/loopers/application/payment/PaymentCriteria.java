package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentCommand;

import java.math.BigDecimal;

public class PaymentCriteria {
    public record Process(
            Long userId,
            BigDecimal amount,
            Long orderId,
            String cardType,
            String cardNo
    ) {
        public PaymentCommand.Process toCommand() {
            return new PaymentCommand.Process(
                    userId,
                    amount,
                    orderId,
                    cardType,
                    cardNo
            );
        }
    }
}
