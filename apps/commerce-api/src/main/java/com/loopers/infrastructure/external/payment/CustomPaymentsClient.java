package com.loopers.infrastructure.external.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "custom-payments", url = "${custom-payments.url}")
public interface CustomPaymentsClient {
    
    @PostMapping("/api/v1/payments")
    PaymentGatewayInfo.ProcessResult processPayment(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PaymentGatewayPayload.Process command
    );
    
    @GetMapping("/api/v1/payments/{transactionId}")
    PaymentGatewayInfo.PaymentDetail getPayment(
            @PathVariable String transactionId,
            @RequestHeader("X-USER-ID") String userId
    );
    
    @GetMapping("/api/v1/payments")
    PaymentGatewayInfo.OrderPayments getPaymentsByOrderId(
            @RequestParam String orderId,
            @RequestHeader("X-USER-ID") String userId
    );
}
