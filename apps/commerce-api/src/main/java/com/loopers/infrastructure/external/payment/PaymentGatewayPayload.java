package com.loopers.infrastructure.external.payment;

public class PaymentGatewayPayload {
    
    public record Process(
            String orderId,
            String cardType,
            String cardNo,
            Long amount,
            String callbackUrl
    ) {}

}
