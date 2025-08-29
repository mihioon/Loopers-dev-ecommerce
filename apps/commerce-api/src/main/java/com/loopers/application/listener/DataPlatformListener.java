package com.loopers.application.listener;

import com.loopers.domain.order.event.OrderCompletedEvent;
import com.loopers.domain.order.event.OrderFailedEvent;
import com.loopers.domain.payment.event.PaymentCompletedEvent;
import com.loopers.domain.payment.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataPlatformListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onOrderCompletedEventListener(OrderCompletedEvent event) {
        log.info("Processing order completed event for data platform: orderId={}, userId={}", 
                event.getOrderId(), event.getUserId());

        try {
            // 플랫폼 전송 - 주문 완료 데이터
            log.info("Successfully sent order completed data to platform: orderId={}, totalAmount={}", 
                    event.getOrderId(), event.getFinalAmount());
            
        } catch (Exception e) {
            log.error("Failed to send order completed data to platform: orderId={}, userId={}, error={}", 
                    event.getOrderId(), event.getUserId(), e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onOrderFailedEventListener(OrderFailedEvent event) {
        log.info("Processing order failed event for data platform: orderId={}, userId={}", 
                event.getOrderId(), event.getUserId());

        try {
            // 플랫폼 전송 - 주문 실패 데이터
            log.info("Successfully sent order failed data to platform: orderId={}, failureReason={}", 
                    event.getOrderId(), event.getFailureReason());
            
        } catch (Exception e) {
            log.error("Failed to send order failed data to platform: orderId={}, userId={}, error={}", 
                    event.getOrderId(), event.getUserId(), e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPaymentCompletedEventListener(PaymentCompletedEvent event) {
        log.info("Processing payment completed event for data platform: paymentId={}, orderId={}", 
                event.getPaymentId(), event.getOrderId());

        try {
            // 플랫폼 전송 - 결제 완료 데이터
            log.info("Successfully sent payment completed data to platform: paymentId={}, amount={}", 
                    event.getPaymentId(), event.getAmount());
            
        } catch (Exception e) {
            log.error("Failed to send payment completed data to platform: paymentId={}, orderId={}, error={}", 
                    event.getPaymentId(), event.getOrderId(), e.getMessage(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPaymentFailedEventListener(PaymentFailedEvent event) {
        log.info("Processing payment failed event for data platform: paymentId={}, orderId={}", 
                event.getPaymentId(), event.getOrderId());

        try {
            // 플랫폼 전송 - 결제 실패 데이터
            log.info("Successfully sent payment failed data to platform: paymentId={}, failureReason={}", 
                    event.getPaymentId(), event.getFailureReason());
            
        } catch (Exception e) {
            log.error("Failed to send payment failed data to platform: paymentId={}, orderId={}, error={}", 
                    event.getPaymentId(), event.getOrderId(), e.getMessage(), e);
        }
    }
}
