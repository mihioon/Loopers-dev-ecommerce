package com.loopers.infrastructure.external.payment;

import java.util.List;

public class PaymentGatewayInfo {
    
    public record Meta(
            String result,
            String errorCode,
            String message
    ) {}
    
    public record ProcessResult(
            Meta meta,
            ProcessData data
    ) {}
    
    public record ProcessData(
            String transactionKey,
            String status
    ) {}
    
    public record PaymentDetail(
            Meta meta,
            PaymentData data
    ) {}
    
    public record PaymentData(
            String transactionKey,
            String orderId,
            String cardType,
            String cardNo,
            Long amount,
            String status,
            String reason
    ) {}
    
    public record OrderPayments(
            Meta meta,
            OrderPaymentData data
    ) {}
    
    public record OrderPaymentData(
            String orderId,
            List<Transaction> transactions
    ) {}
    
    public record Transaction(
            String transactionKey,
            String status,
            String reason
    ) {}
}
