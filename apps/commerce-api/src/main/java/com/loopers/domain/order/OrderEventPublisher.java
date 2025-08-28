package com.loopers.domain.order;

import com.loopers.domain.order.event.OrderCreatedEvent;
import com.loopers.domain.order.event.OrderCompletedEvent;
import com.loopers.domain.order.event.OrderFailedEvent;

public interface OrderEventPublisher {
    void publishOrderCreated(OrderCreatedEvent event);
    void publishOrderCompleted(OrderCompletedEvent event);
    void publishOrderFailed(OrderFailedEvent event);
}
