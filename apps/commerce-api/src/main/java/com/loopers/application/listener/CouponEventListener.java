package com.loopers.application.listener;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponEventListener {
    
    private final CouponService couponService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onOrderCompletedEventListener(OrderCompletedEvent event) {
        log.info("Processing order completed event for coupon usage: orderId={}, userId={}", 
                event.getOrderId(), event.getUserId());

        try {
            if (event.getCouponIds() != null && !event.getCouponIds().isEmpty()) {
                couponService.useCoupons(event.getUserId(), event.getCouponIds());
                log.info("Successfully processed coupon usage: orderId={}, couponIds={}", 
                        event.getOrderId(), event.getCouponIds());
            } else {
                log.debug("No coupons to process for order: orderId={}", event.getOrderId());
            }
            
        } catch (Exception e) {
            log.error("Failed to process coupon usage: orderId={}, userId={}, couponIds={}, error={}", 
                    event.getOrderId(), event.getUserId(), event.getCouponIds(), e.getMessage(), e);
            // 쿠폰 사용 실패 시 보상 트랜잭션 필요
        }
    }
}
