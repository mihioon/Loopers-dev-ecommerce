package com.loopers.application.listener;

import com.loopers.domain.like.event.LikeAddedEvent;
import com.loopers.domain.like.event.LikeRemovedEvent;
import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.order.event.OrderCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class UserBehaviorTrackingListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void trackLikeAdded(LikeAddedEvent event) {
        log.info("[USER_BEHAVIOR] Like Added - userId={}, productId={}, timestamp={}", 
                event.getUserId(), event.getProductId(), event.getOccurredOn());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void trackLikeRemoved(LikeRemovedEvent event) {
        log.info("[USER_BEHAVIOR] Like Removed - userId={}, productId={}, timestamp={}", 
                event.getUserId(), event.getProductId(), event.getOccurredOn());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void trackOrderCreated(OrderCreatedEvent event) {
        log.info("[USER_BEHAVIOR] Order Created - userId={}, orderId={}, totalAmount={}, itemCount={}, timestamp={}", 
                event.getUserId(), event.getOrderId(), event.getTotalAmount(), 
                event.getOrderItems().size(), event.getOccurredOn());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void trackOrderCompleted(OrderCompletedEvent event) {
        log.info("[USER_BEHAVIOR] Order Completed - userId={}, orderId={}, finalAmount={}, timestamp={}", 
                event.getUserId(), event.getOrderId(), event.getFinalAmount(), event.getOccurredOn());
    }
}
