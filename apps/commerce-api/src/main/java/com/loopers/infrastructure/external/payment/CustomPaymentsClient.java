package com.loopers.infrastructure.external.payment;

import com.loopers.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "custom-payments", url = "${custom-payments.url}", 
    configuration = {FeignConfig.class})
public interface CustomPaymentsClient {
    
    @Retry(name = "pgRetry")
    @CircuitBreaker(name = "pgCircuit")
    @PostMapping("/api/v1/payments")
    PaymentGatewayInfo.ProcessResult processPayment(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PaymentGatewayPayload.Process command
    );
    
    @Retry(name = "pgRetry")
    @CircuitBreaker(name = "pgCircuit")
    @GetMapping("/api/v1/payments/{transactionId}")
    PaymentGatewayInfo.PaymentDetail getPayment(
            @PathVariable String transactionId,
            @RequestHeader("X-USER-ID") String userId
    );
    
    @Retry(name = "pgRetry")
    @CircuitBreaker(name = "pgCircuit")
    @GetMapping("/api/v1/payments")
    PaymentGatewayInfo.OrderPayments getPaymentsByOrderId(
            @RequestParam String orderId,
            @RequestHeader("X-USER-ID") String userId
    );
}
