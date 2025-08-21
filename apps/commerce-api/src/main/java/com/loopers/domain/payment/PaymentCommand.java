package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentCommand {
    
    public record Process(
            Long userId,
            BigDecimal amount,
            String orderUuid,
            Long orderId,
            String cardType,
            String cardNo
    ) {}

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
