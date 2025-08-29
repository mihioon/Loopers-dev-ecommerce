package com.loopers.application.listener;

import com.loopers.domain.order.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataPlatformListener {

    @EventListener
    @Async
    public void onOrderCompletedEventListener(OrderCompletedEvent event) {
        log.info("Processing order completed event for data platform: orderId={}, userId={}", 
                event.getOrderId(), event.getUserId());

        try {
            // 플랫폼 전송
            log.info("Successfully sent order data to platform: orderId={}, totalAmount={}", 
                    event.getOrderId(), event.getTotalAmount());
            
        } catch (Exception e) {
            log.error("Failed to send order data to platform: orderId={}, userId={}, error={}", 
                    event.getOrderId(), event.getUserId(), e.getMessage(), e);
            // 데이터 플랫폼 전송 실패는 주문 완료를 방해하지 않음
        }
    }
}