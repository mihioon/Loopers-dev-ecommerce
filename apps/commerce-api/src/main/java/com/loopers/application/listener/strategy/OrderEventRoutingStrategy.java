package com.loopers.application.listener.strategy;

import com.loopers.domain.common.event.DomainEvent;
import com.loopers.domain.order.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventRoutingStrategy implements EventRoutingStrategy {
    
    @Override
    public boolean supports(Class<? extends DomainEvent> eventClass) {
        return OrderEvent.class.isAssignableFrom(eventClass);
    }
    
    @Override
    public MessageRoute route(DomainEvent event) {
        OrderEvent orderEvent = (OrderEvent) event;
        String stream = "order-events";
        String key = "order:" + orderEvent.getDomainId();
        return MessageRoute.of(stream, key);
    }
}
