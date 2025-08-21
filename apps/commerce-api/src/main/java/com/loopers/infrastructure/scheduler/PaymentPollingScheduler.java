package com.loopers.infrastructure.scheduler;

import com.loopers.application.payment.PaymentSyncFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentPollingScheduler {
    
    private final PaymentSyncFacade paymentSyncFacade;
    
    @Scheduled(fixedRate = 3000)
    public void pollPendingPayments() {
        try {
            List<String> pendingPaymentIds = paymentSyncFacade.findPendingPaymentIds();
            
            if (pendingPaymentIds.isEmpty()) {
                return;
            }
            
            for (String paymentId : pendingPaymentIds) {
                try {
                    paymentSyncFacade.syncPaymentStatus(paymentId);
                } catch (Exception e) {}
                
                try {
                    paymentSyncFacade.processCompletedPayment(paymentId);
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
    }
}
