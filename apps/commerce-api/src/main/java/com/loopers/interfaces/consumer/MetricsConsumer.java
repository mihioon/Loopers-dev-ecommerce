package com.loopers.interfaces.consumer;

import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.common.messaging.IdempotencyService;
import com.loopers.domain.like.event.LikeAddedEvent;
import com.loopers.domain.like.event.LikeRemovedEvent;
import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.domain.order.event.OrderCompletedEvent;
import com.loopers.domain.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsConsumer {
    
    private final ProductMetricsService productMetricsService;
    private final IdempotencyService idempotencyService;

    @KafkaListener(
        topics = {"product-view-events", "like-events", "order-events"},
        containerFactory = "kafkaListenerContainerFactory",
        groupId = ConsumerGroupConstants.METRICS_AGGREGATOR
    )
    @Transactional
    public void handleEvent(ConsumerRecord<String, DomainEvent> record, Acknowledgment ack) {
        DomainEvent event = record.value();
        
        log.debug("MetricsListener received event: topic={}, key={}, eventId={}", 
                 record.topic(), record.key(), event.getEventId());

        // 멱등성 체크
        if (idempotencyService.isProcessed(event.getEventId(), ConsumerGroupConstants.METRICS_AGGREGATOR)) {
            log.debug("Event already processed by MetricsListener: eventId={}", event.getEventId());
            ack.acknowledge();
            return;
        }

        try {
            // 타입별 분기 처리
            if (event instanceof LikeAddedEvent) {
                handleLikeAddedEvent((LikeAddedEvent) event);
            } else if (event instanceof LikeRemovedEvent) {
                handleLikeRemovedEvent((LikeRemovedEvent) event);
            } else if (event instanceof ProductViewedEvent) {
                handleProductViewedEvent((ProductViewedEvent) event);
            } else if (event instanceof OrderCompletedEvent) {
                handleOrderCompletedEvent((OrderCompletedEvent) event);
            } else {
                log.debug("Unsupported event type for metrics: {}", event.getEventType());
            }
            
            idempotencyService.markAsProcessed(event.getEventId(), ConsumerGroupConstants.METRICS_AGGREGATOR);
            
            log.debug("Event processed by MetricsListener: eventId={}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process event in MetricsListener: eventId={}", event.getEventId(), e);
            throw e;
        }

        ack.acknowledge();
    }

    private void handleLikeAddedEvent(LikeAddedEvent event) {
        LocalDate date = event.getOccurredOn().toLocalDate();
        productMetricsService.incrementLikeCount(event.getProductId(), date, 1);
        log.debug("Like added: productId={}, date={}", event.getProductId(), date);
    }

    private void handleLikeRemovedEvent(LikeRemovedEvent event) {
        LocalDate date = event.getOccurredOn().toLocalDate();
        productMetricsService.incrementLikeCount(event.getProductId(), date, -1);
        log.debug("Like removed: productId={}, date={}", event.getProductId(), date);
    }

    private void handleProductViewedEvent(ProductViewedEvent event) {
        LocalDate date = event.getViewedAt().toLocalDate();
        productMetricsService.incrementViewCount(event.getProductId(), date, 1);
        log.debug("Product viewed: productId={}, date={}", event.getProductId(), date);
    }

    private void handleOrderCompletedEvent(OrderCompletedEvent event) {
        LocalDate date = event.getOccurredOn().toLocalDate();
        
        // 주문 완료 이벤트에서 각 상품별로 판매량 집계
        for (OrderCompletedEvent.OrderItemInfo item : event.getOrderItems()) {
            productMetricsService.incrementSalesCount(item.getProductId(), date, item.getQuantity(), item.getPrice());
            log.debug("Sales updated: productId={}, quantity={}, amount={}, date={}", 
                     item.getProductId(), item.getQuantity(), item.getPrice(), date);
        }
    }
}
