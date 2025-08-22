package com.loopers.application.payment;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentRepository;
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
    private final OrderFacade orderFacade;
    private final OrderService orderService;
    
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
        
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            return;
        }
        
        if (payment.getTransactionKey() == null) {
            return;
        }
        
        PaymentGatewayInfo.PaymentDetail paymentDetail = 
            paymentGatewayAdapter.getPayment(payment.getTransactionKey(), systemUserId);
        
        updatePaymentStatus(payment, paymentDetail.data().status());
    }
    
    private void updatePaymentStatus(Payment payment, String pgStatus) {
        switch (pgStatus.toUpperCase()) {
            case "SUCCESS" -> payment.complete();
            case "FAILED" -> payment.fail();
            case "PENDING" -> {}
            default -> {}
        }
    }
    
    @Transactional
    public void processCompletedPayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentUuid(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            return;
        }
        
        var orderInfo = orderService.getOrder(payment.getOrderId());
        if (orderService.isAlreadyCompleted(orderInfo.orderUuid())) {
            return;
        }
        var criteria = new OrderCriteria.Complete(
            payment.getOrderId(),
            payment.getUserId(), 
            orderInfo.couponIds()
        );
        orderFacade.completeOrder(criteria);
    }
}
