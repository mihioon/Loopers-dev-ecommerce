package com.loopers.infrastructure.scheduler;

import com.loopers.application.payment.PaymentPollingService;
import com.loopers.application.payment.PaymentSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentPollingScheduler {
    
    private final PaymentSyncFacade paymentSyncFacade;
    private final PaymentPollingService paymentPollingService;
    
    @Scheduled(fixedRate = 3000)
    public void pollPendingPayments() {
        List<String> pendingPaymentIds = paymentSyncFacade.findPendingPaymentIds();
        
        if (pendingPaymentIds.isEmpty()) {
            return;
        }
        
        for (String paymentId : pendingPaymentIds) {
            paymentPollingService.syncPaymentStatus(paymentId);
            paymentPollingService.processCompletedPayment(paymentId);
        }
    }
    
}
