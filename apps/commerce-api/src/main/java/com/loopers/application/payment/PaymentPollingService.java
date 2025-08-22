package com.loopers.application.payment;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentPollingService {
    
    private final PaymentSyncFacade paymentSyncFacade;
    
    @Retry(name = "pgRetry")
    @CircuitBreaker(name = "pgCircuit")
    public void syncPaymentStatus(String paymentId) {
        paymentSyncFacade.syncPaymentStatus(paymentId);
    }
    
    @Retry(name = "pgRetry")
    @CircuitBreaker(name = "pgCircuit")
    public void processCompletedPayment(String paymentId) {
        paymentSyncFacade.processCompletedPayment(paymentId);
    }
}
