package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentCommand;
import com.loopers.infrastructure.external.payment.PaymentGatewayPayload;

import java.math.BigDecimal;

public class PaymentCriteria {
    public record Process(
            Long userId,
            BigDecimal amount,
            Long orderId,
            String cardType,
            String cardNo
    ) {
        public PaymentCommand.Process toCommand(String orderUuid) {
            return new PaymentCommand.Process(
                    userId,
                    amount,
                    orderUuid,
                    orderId,
                    cardType,
                    cardNo
            );
        }

        public PaymentGatewayPayload.Process toGatewayCommand(String orderUuid, String callbackUrl) {
            return new PaymentGatewayPayload.Process(
                    orderUuid,
                    cardType,
                    cardNo,
                    amount.longValue(),
                    callbackUrl
            );
        }
    }

    public record Callback(
            String transactionKey,
            String orderUuid,
            String cardType,
            String cardNo,
            BigDecimal amount,
            String status,
            String reason
    ) {}
}
