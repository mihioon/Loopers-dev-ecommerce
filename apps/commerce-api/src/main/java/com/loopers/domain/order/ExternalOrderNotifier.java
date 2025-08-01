package com.loopers.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExternalOrderNotifier {
    
    public void notifyOrderCreated(Order order) {
        // Mock 구현 - 실제로는 외부 API 호출
        log.info("외부 시스템에 주문 생성 알림 전송 - 주문 ID: {}, 사용자 ID: {}, 총액: {}", 
                order.getId(), order.getUserId(), order.getTotalAmount());
        
        // 실제 구현 시에는 외부 API 호출 또는 메시지 큐 전송
        simulateExternalApiCall(order);
    }
    
    private void simulateExternalApiCall(Order order) {
        try {
            // 외부 API 호출 시뮬레이션
            Thread.sleep(100);
            log.info("외부 시스템 알림 완료 - 주문 ID: {}", order.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("외부 시스템 알림 중 인터럽트 발생 - 주문 ID: {}", order.getId());
        }
    }
}