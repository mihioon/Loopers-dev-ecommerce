package com.loopers.application.payment;

import com.loopers.domain.common.event.EventPublisher;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.event.PaymentFailedEvent;
import com.loopers.domain.payment.event.PaymentCompletedEvent;
import com.loopers.infrastructure.external.payment.PaymentGatewayAdapter;
import com.loopers.infrastructure.external.payment.PaymentGatewayInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentSyncFacade {
    
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayAdapter paymentGatewayAdapter;
    private final EventPublisher eventPublisher;
    
    @Value("${custom-payments.user-id}")
    private String systemUserId;
    
    @Transactional(readOnly = true)
    public List<String> findPendingPaymentIds() {
        LocalDateTime fiveSecondsAgo = LocalDateTime.now().minusSeconds(5);
        LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);
        
        return paymentRepository.findPendingPaymentIds(fiveSecondsAgo, threeMinutesAgo);
    }
    
    @Transactional
    public void syncPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findByPaymentUuid(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        
        if (payment.getTransactionKey() == null) {
            return;
        }
        
        PaymentGatewayInfo.PaymentDetail paymentDetail = 
            paymentGatewayAdapter.getPayment(payment.getTransactionKey(), systemUserId);
        
        updatePaymentStatus(payment, paymentDetail.data().status());
    }
    
    private void updatePaymentStatus(Payment payment, String pgStatus) {
        switch (pgStatus.toUpperCase()) {
            case "SUCCESS" -> {
                payment.complete();
                processSuccessPayment(payment);
            }
            case "FAILED" -> {
                payment.fail();
                processFailedPayment(payment);
            }
            case "PENDING" -> {}
            default -> {}
        }
    }
    
    private void processFailedPayment(Payment payment) {
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                payment.getPaymentUuid(),
                payment.getOrderId(),
                payment.getAmount(),
                "Payment processing failed",
                "PAYMENT_FAILED"
        );
        
        eventPublisher.publish(paymentFailedEvent);
    }
    
    private void processSuccessPayment(Payment payment) {
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(
                payment.getPaymentUuid(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getTransactionKey(),
                LocalDateTime.now()
        );
        
        eventPublisher.publish(paymentCompletedEvent);
    }
}
